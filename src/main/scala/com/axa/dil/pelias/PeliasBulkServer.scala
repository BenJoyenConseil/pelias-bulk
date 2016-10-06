package com.axa.dil.pelias

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.typesafe.config.ConfigFactory

import scala.util.Try

class  PeliasBulkServer extends HttpServer {
  val config = ConfigFactory.load()

  protected override def defaultFinatraHttpPort: String = s":${Try(config.getInt("pelias.bulk.server.port")).getOrElse(8080)}"

  override def defaultHttpPort: Int = Try(config.getInt("pelias.bulk.server.adminPort")).getOrElse(9090)

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add(new PeliasBulkController(Try(config.getString("pelias.node.server")).getOrElse("compute1:3100")))
  }
}

object PeliasBulkServerMain extends PeliasBulkServer