package com.useinsider.model

import spray.json.DefaultJsonProtocol

case class Book(id: Option[Long], title: String, author: String, taxonomy: String)

object Book extends DefaultJsonProtocol {
  implicit val bookFormat = jsonFormat4(Book.apply)
}
