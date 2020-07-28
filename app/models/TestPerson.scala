package models

import anorm.{Macro, NamedParameter, SQL, SqlParser}
import Macro.ColumnNaming
import play.api.db.Database

import scala.util.Try

case class TestPerson(personId: Long, firstName: String, lastName: String)

object TestPerson {
  val parser = Macro.namedParser[TestPerson](ColumnNaming.SnakeCase)

  def selectAll(implicit database: Database): List[TestPerson] =
    database.withConnection { implicit conn =>
    SQL("SELECT * FROM SMARTIVR.TEST_PERSON")
      .as(parser.*)
  }

  def selectFilterById(id: Long)(implicit database: Database): TestPerson =
    database.withConnection { implicit conn =>
    SQL("SELECT * FROM SMARTIVR.TEST_PERSON where person_id = {id}")
      .on("id" -> id)
      .executeQuery()
      .as(parser.single)
  }

  def insert(firstName: String, lastName: String)(implicit database: Database): Try[Long] =
    database.withConnection { implicit conn =>
    SQL("insert into SMARTIVR.TEST_PERSON(first_name, last_name) values({first_name}, {last_name})")
      .on("first_name" -> firstName, "last_name" -> lastName)
      .executeInsert1("person_id")(SqlParser.scalar[Long].single)
  }

  def delete(id: Long)(implicit database: Database) =
    database.withConnection { implicit conn =>
    SQL("DELETE FROM SMARTIVR.TEST_PERSON where person_id = {id}")
      .on("id" -> id)
      .executeUpdate()
  }

  def update(setColumn: String, setValue: String, whereColumn: String, whereValue: String)
            (implicit database: Database): Int =
    database.withConnection { implicit conn =>
      val nps = Seq[NamedParameter] (
        "setValue" -> setValue,
        "whereValue" -> whereValue
      )

      SQL(s"UPDATE SMARTIVR.TEST_PERSON SET ${setColumn} = {setValue} WHERE ${whereColumn} = {whereValue}")
        .on(nps: _*)
        .executeUpdate()
    }

}
