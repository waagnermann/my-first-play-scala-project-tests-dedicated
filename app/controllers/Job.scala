package controllers

import javax.inject.{Inject, Singleton}
import models.TestJob
import play.api.db.Database
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}
import io.swagger.annotations.Api

@Singleton
class Job @Inject()(implicit db: Database, val controllerComponents: ControllerComponents)
  extends BaseController {

  def get(id: Long) = Action { implicit request: Request[AnyContent] =>

    val testJobOpt = TestJob.listEmployeesByJob(id)
    testJobOpt match {
      case Some(testJob) => Ok(views.html.job(testJob))
      case None => Ok(s"There is now job with id = $id")
    }
  }
}
