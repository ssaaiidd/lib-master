package com.useinsider.model

import slick.jdbc.PostgresProfile.api._

object Tables {
  class Library(tag: Tag) extends Table[Book](tag, "book_list") {
    def id = column[Option[Long]]("book_id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("book_title")
    def author = column[String]("book_author")
    def taxonomy = column[String]("book_taxonomy")

    def * =
      (id, title, author, taxonomy) <> ((Book.apply _).tupled, Book.unapply)
  }

  val library = TableQuery[Library]
}
