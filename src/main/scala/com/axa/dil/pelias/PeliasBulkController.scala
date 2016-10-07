package com.axa.dil.pelias

import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finatra.http.Controller
import com.twitter.util.{Future, Futures}

import scala.collection.JavaConversions._

case class Address(text: String, size: Int)

class PeliasBulkController(peliasHostURL: String) extends Controller() {
  val peliasService = Http.newService(peliasHostURL)
  val api = "/v1/search"

  post(api) {
    addressList: List[Address] =>
      val responseList: List[Future[Response]] = addressList.map {
        address =>
          val req = Request.queryString(api, Map("text" -> address.text, "size" -> address.size.toString))
          peliasService(Request(Method.Get, req))
      }
      Futures.collect(responseList).map{
        res => response.ok.json(res.map(result => result.getContentString()).mkString("[", ",", "]"))
      }
  }
}