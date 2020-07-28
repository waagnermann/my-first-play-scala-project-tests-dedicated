package models

import anorm.{SQL, ~}
import anorm.SqlParser.{flatten, long, str}
import com.moscoworg.JobCarcass.database
import io.swagger.annotations.{ApiModel, ApiModelProperty}

@ApiModel
case class TestJob(id: Long,
                    @ApiModelProperty(value = "name", dataType = "String", example = "developer") name: String,
                    personnel: List[TestPerson])

object TestJob {

  val jobIdAndNameParser = long("id") ~ str("name") map {
    case id ~ name => TestJob(id, name, List.empty[TestPerson])
  }
  val parser = jobIdAndNameParser ~ TestPerson.parser.?

  def listEmployeesByJob(id: Long) = database.withConnection { implicit conn =>
    val query = SQL("""select j.ID, j.NAME, p.PERSON_ID, p.FIRST_NAME, p.LAST_NAME
        from TEST_JOB j
        left join TEST_PERSON p on j.ID = p.TEST_JOB_ID
        where j.ID = {id}""").on("id" -> id)
    val jobEntries = query.as(parser.*)
    val jobsView = jobEntries.view groupBy(_._1) mapValues(_ flatMap(_._2)) map {
      case (TestJob(id, job, _), personnel) => TestJob(id, job, personnel.toList)
    }

    jobsView.headOption
  }
}

