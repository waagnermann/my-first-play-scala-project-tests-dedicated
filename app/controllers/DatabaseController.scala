package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import anorm._

@Singleton
class DatabaseController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def invoke: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.invoke("Nothing`s fetched unfortunately")(java.time.LocalDateTime.now))
  }

}
