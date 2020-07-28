package com.moscoworg

import models.{TestJob, TestPerson}
import play.api.db.Databases
import anorm.{SQL, ~}
import anorm.SqlParser.{flatten, long, str}

case class JobCarcass(id: Long, jobName: String, personnel: List[TestPerson])

object JobCarcass {
  val database = Databases(
    driver = "oracle.jdbc.OracleDriver",
    url = "jdbc:oracle:thin:@10.172.153.34:1522/gen",
    config = Map(
      "username" -> "SmartIVR",
      "password" -> "r1919xVa_hwe",
      "maximumPoolSize" -> 5,
      "logSql" -> true
    )
  )

  val jobIdAndNameParser = (long("id") ~ str("name")) map {
    case id ~ name => TestJob(id, name, List.empty[TestPerson])
  }
  val jobEntryParser = jobIdAndNameParser ~ TestPerson.parser.?

    def listEmployeesByJob(id: Long) = database.withConnection { implicit conn =>
    val query = SQL("""select j.ID, j.NAME, p.PERSON_ID, p.FIRST_NAME, p.LAST_NAME
        from TEST_JOB j
        left join TEST_PERSON p on j.ID = p.TEST_JOB_ID
        where j.ID = {id}""").on("id" -> id)

      val jobEntries = query.as(jobEntryParser.*)

      val jobsView = jobEntries.view groupBy(_._1) mapValues(_ flatMap(_._2)) map {
        case (TestJob(id, job, _), personnel) => TestJob(id, job, personnel.toList)
      }

      jobsView.headOption
  }

  def main(args: Array[String]): Unit = {
    println(listEmployeesByJob(2))
  }
}

