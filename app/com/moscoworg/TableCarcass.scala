package com.moscoworg

import anorm.Macro.ColumnNaming
import anorm.SqlParser.{int, long, scalar, str}
import anorm.{Macro, NamedParameter, SQL, ~}
import play.api.db.Databases

case class TableCarcass(personId: Long, firstName: String, lastName: String)

object TableCarcass {
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

  val defaultParser = int("person_id") ~
    str("first_name") ~
    str("last_name") map { case id ~ first_name ~ last_name =>
    TableCarcass(id, first_name, last_name)}

  val parser = Macro.namedParser[TableCarcass](ColumnNaming.SnakeCase)

  def selectLastnameById(person_id: Long) = database.withConnection(autocommit = true){ implicit conn =>
    SQL("SELECT last_name FROM SMARTIVR.TEST_PERSONS where person_id = {person_id}")
      .on("person_id" -> person_id).as(scalar[String].single)
  }

  def selectFirstnameWhereIdIn(seq: Seq[Long]) = database.withConnection(autocommit = true){ implicit conn =>
    SQL("SELECT first_name FROM SMARTIVR.TEST_PERSONS where person_id IN ({ids})").
      on("ids" -> seq)
      .as(scalar[String].*)
  }

  def selectTable = database.withConnection(autocommit = true) { implicit conn =>
    SQL("SELECT * FROM SMARTIVR.TEST_PERSONS")
      .as((long("person_id") ~ str("first_name") ~ str("last_name")).map {
        case p ~ f ~ l => (p, f, l)
      }.*)
  }

  def selectFilterById(id: Long): TableCarcass = database.withConnection(autocommit = true){ implicit conn =>
    SQL("SELECT * FROM SMARTIVR.TEST_PERSONS where person_id = {id}")
      .on("id" -> id)
      .executeQuery()
      .as(parser.single)
  }

  def selectAll: List[TableCarcass] = database.withConnection { implicit conn =>
    SQL("SELECT * FROM SMARTIVR.TEST_PERSON")
      .as(parser.*)
  }

  def delete(id: Long) = database.withConnection(autocommit = true){ implicit conn =>
    SQL("DELETE FROM SMARTIVR.TEST_PERSONS where person_id = {id}")
      .on("id" -> id)
      .executeUpdate()
  }

  def update(setColumn: String, setValue: String, whereColumn: String, whereValue: String): Int =
    database.withConnection { implicit conn =>
      val nps = Seq[NamedParameter] (
        "setValue" -> setValue,
        "whereValue" -> whereValue
      )

      SQL(s"UPDATE SMARTIVR.TEST_PERSON SET ${setColumn} = {setValue} WHERE ${whereColumn} = {whereValue}")
        .on(nps: _*)
        .executeUpdate()
    }

  def main(args: Array[String]): Unit = {
    println(update("first_name", "Anton", "last_name", "Petrov"))
  }

}
