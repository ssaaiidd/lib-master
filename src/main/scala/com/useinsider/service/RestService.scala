package com.useinsider.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.useinsider.model.Book
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import scala.util.{Failure, Success}

class RestService(dbService: DbService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = get {
    path("library") {
      complete(dbService.getLibrary)
    } ~ path("book" / LongNumber) { bookId =>
      val book = dbService.getBook(bookId)
      onComplete(book) {
        case Success(theBook) => complete(book)
        case Failure(e) => {
          logger.error(s"The book not found", e)
          complete(StatusCodes.NotFound)
        }
      }
    } ~ path("book") {
      parameters("author") { author =>
        val book = dbService.getBooksWithAuthor(author)
        onComplete(book) {
          case Success(theBook) => complete(book)
          case Failure(e) => {
            logger.error(s"The book not found", e)
            complete(StatusCodes.NotFound)
          }
        }
      } ~ parameters("taxonomy") { taxonomy =>
        val book = dbService.getBooksWithTaxonomy(taxonomy)
        onComplete(book) {
          case Success(theBook) => complete(book)
          case Failure(e) => {
            logger.error(s"The book not found", e)
            complete(StatusCodes.NotFound)
          }
        }
      } 
    }
  } ~
    post {
      path("book") {
        entity(as[Book]) { book =>
          val saved = dbService.addBook(book.title, book.author, book.taxonomy)
          onComplete(saved) {
            case Success(savedBook) => complete(savedBook)
            case Failure(e) => {
              logger.error(s"Failed to insert a book ${book.id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path("book" / LongNumber) { id =>
        entity(as[Book]) { book =>
          val updated =
            dbService.updateBook(id, book.title, book.author, book.taxonomy)
          onComplete(updated) {
            case Success(updatedRows) =>
              complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to update a book ${id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
}
