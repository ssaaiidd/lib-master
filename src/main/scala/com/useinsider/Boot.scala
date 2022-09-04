package com.useinsider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.useinsider.service.{DbService, RestService}
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.PostgresProfile.api._

object Boot extends App with StrictLogging {
  private implicit val system       = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private implicit val ec           = system.dispatcher

  val db = Database.forConfig("herokuPostgresql")

  val dbService   = new DbService(db)
  val restService = new RestService(dbService)

  Http()
    .bindAndHandle(restService.route, "localhost", 3002)
    .map(_ => logger.info("Server started at port 3002"))
}