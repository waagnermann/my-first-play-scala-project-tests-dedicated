package controllers

import java.lang.annotation.Annotation
import java.sql.SQLSyntaxErrorException

import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation, ApiParam, ApiResponse, ApiResponses, Example, ResponseHeader}
import javax.inject.{Inject, Singleton}
import models.TestPerson
import play.api.db.Database
import play.api.libs.json.{JsResultException, JsValue}
import play.api.mvc.{AnyContent, BaseController, BodyParsers, ControllerComponents, Request}

import scala.util.{Failure, Success}

@Singleton
@Api(value = "Job", protocols = "http")
class Person @Inject()(implicit db: Database, val controllerComponents: ControllerComponents)
  extends BaseController {

  @ApiOperation(
    value = "Получить список всех персон в системе",
    produces = "text/html",
    protocols = "http"
  )
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Список всех персон в системе"),
    new ApiResponse(code = 500, message = "Долгое соединение с базой данныx")
  ))
  def all = Action { implicit request: Request[AnyContent] =>
    val records = TestPerson.selectAll
    Ok(views.html.person("all", records))
  }

  @ApiOperation(
    value = "Получить работника по id в системе",
    produces = "text/html",
    protocols = "http"
  )
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Успешное завершение запроса"),
    new ApiResponse(code = 500, message = "Внутренняя ошибка сервера")
  ))
  def get(@ApiParam(value = "Внутренний идентификатор сотрудника") id: Long) = Action { implicit request: Request[AnyContent] =>
    val records = TestPerson.selectFilterById(id)
    Ok(views.html.person("queried", List(records)))
  }

  @ApiOperation(
    value = "Bнести работника в систему",
    produces = "text/html",
    protocols = "http"
  )
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Успешное завершение запроса"),
    new ApiResponse(code = 500, message = "Внутренняя ошибка сервера"),
    new ApiResponse(code = 400, message = "Невалидный запрос")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "json",
      value = "Фамилия и нового сотрудника",
      required = true,
      dataType = "com.jsonbody.TestPersonName", // complete path
      paramType = "body"
    )
  ))
  def save = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody match {
      case Some(json) =>
        try {
          val (firstName, lastName) = ((json \ "firstName").as[String], (json \ "lastName").as[String])
          val tryContainer = TestPerson.insert(firstName, lastName)
          tryContainer match {
            case Success(id) => Ok(s"Person is assigned with id = $id")
            case Failure(exception) => InternalServerError(s"Failed with ${exception.toString}")
          }
      }
        catch {
          case error: JsResultException => BadRequest(error.toString + "\n")
        }
      case None => BadRequest("No json-body provided\n")
    }
  }

  @ApiOperation(
    value = "Удаление работника из системы",
    produces = "text/plain",
    protocols = "http"
  )
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Успешное завершение запроса"),
    new ApiResponse(code = 500, message = "Внутренняя ошибка сервера"),
    new ApiResponse(code = 400, message = "Невалидный запрос")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "json",
      value = "id сотрудника",
      required = true,
      dataType = "com.jsonbody.TestPersonId",
      paramType = "body"
    )
  ))
  def delete() = Action { implicit request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody match {
      case Some(json) =>
        try {
          val id = (json \ "personId").as[Long]
          val rowsNum = TestPerson.delete(id)
          Ok(s"Number of rows deleted: $rowsNum\n")
        }
        catch {
          case error: JsResultException => BadRequest(error.toString + "\n")
        }

      case None => BadRequest("No json-body provided\n")
    }
  }

  @ApiOperation(
    value = "Обновление данных работника",
    produces = "text/plain",
    protocols = "http"
  )
  @ApiResponses(value = Array(
    new ApiResponse(code = 200, message = "Успешное завершение запроса"),
    new ApiResponse(code = 500, message = "Внутренняя ошибка сервера"),
    new ApiResponse(code = 400, message = "Невалидный запрос")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "json",
      value = "id сотрудника",
      required = true,
      dataType = "com.jsonbody.UpdatePersonSchema",
      paramType = "body"
    )
  ))
  def update = Action { implicit request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody match {
      case Some(json) =>
        try {
          val (setColumn, setValue, whereColumn, whereValue) =
            (
              (json \ "set" \ "column").as[String], (json \ "set" \ "value").as[String],
              (json \ "where" \ "column").as[String], (json \ "where" \ "value").as[String]
            )
          try {
            val rowsNum = TestPerson.update(setColumn, setValue, whereColumn, whereValue)
            Ok(s"Number of rows updated: $rowsNum\n")
          }
          catch {
            case error: SQLSyntaxErrorException => InternalServerError(error.toString)
          }
        }
        catch {
          case error: JsResultException => BadRequest(error.toString + "\n")
        }

      case None => BadRequest("No json-body provided\n")
    }
  }

}
