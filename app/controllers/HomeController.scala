package controllers

import javax.inject._
import play.api.db.Database
import play.api.mvc._
import anorm._
import anorm.SqlParser.str
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(db: Database, val controllerComponents: ControllerComponents)
  extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index("Hello"))

  }

  def redirectDocs: Action[AnyContent] = Action {
    Redirect(
      url = "/assets/lib/swagger-ui/index.html",
      queryString = Map("url" -> Seq("/swagger.json"))
    )
  }
}
