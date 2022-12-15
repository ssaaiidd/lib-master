package com.useinsider.service

import com.useinsider.model.Book
import slick.jdbc.PostgresProfile.api._
import com.useinsider.model.Tables._

import scala.concurrent.{ExecutionContext, Future}
trait LibraryRepository {
    def getLibrary(): Future[Seq[Book]]
    def getBook(id: Long): Future[Option[Book]]
    def getBooksWithAuthor(author: String): Future[Option[Book]]
    def getBooksWithTaxonomy(taxonomy: String): Future[Option[Book]]
    def addBook(title: String, author: String, taxonomy: String): Future[Book]
    def updateBook(id: Long, title: String, author: String, taxonomy: String): Future[Int] 
}
class DbService(db: Database)(implicit ec: ExecutionContext) extends LibraryRepository {
  def getLibrary: Future[Seq[Book]] = db.run(library.result)
  def getBook(id: Long): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.id === id).result.headOption
    }
  }
  def getBooksWithAuthor(author: String): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.author === author).result.headOption
    }
  }
  def getBooksWithTaxonomy(taxonomy: String): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.taxonomy === taxonomy).result.headOption
    }
  }
  def addBook(title: String, author: String, taxonomy: String): Future[Book] = {
    val action =
      (library.map(book => (book.title, book.author, book.taxonomy)) returning library
        .map(_.id) into ((bookInfo, id) =>
        Book(id, bookInfo._1, bookInfo._2, bookInfo._3)
      )) += (title, author, taxonomy)
    db.run(action)
  }
  def updateBook(id: Long, title: String, author: String, taxonomy: String): Future[Int] = {
    db.run {
      library
        .filter(book => book.id === id)
        .map(book => (book.id, book.title, book.author, book.taxonomy))
        .update((Some(id), title, author, taxonomy))
    }
  }
}

class InMemoryDBService(db: Database)(implicit ec: ExecutionContext) extends LibraryRepository {
  def getLibrary: Future[Seq[Book]] = db.run(library.result)
  def getBook(id: Long): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.id === id).result.headOption
    }
  }
  def getBooksWithAuthor(author: String): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.author === author).result.headOption
    }
  }
  def getBooksWithTaxonomy(taxonomy: String): Future[Option[Book]] = {
    db.run {
      library.filter(book => book.taxonomy === taxonomy).result.headOption
    }
  }
  def addBook(title: String, author: String, taxonomy: String): Future[Book] = {
    val action =
      (library.map(book => (book.title, book.author, book.taxonomy)) returning library
        .map(_.id) into ((bookInfo, id) =>
        Book(id, bookInfo._1, bookInfo._2, bookInfo._3)
      )) += (title, author, taxonomy)
    db.run(action)
  }
  def updateBook(id: Long, title: String, author: String, taxonomy: String): Future[Int] = {
    db.run {
      library
        .filter(book => book.id === id)
        .map(book => (book.id, book.title, book.author, book.taxonomy))
        .update((Some(id), title, author, taxonomy))
    }
  }
}
