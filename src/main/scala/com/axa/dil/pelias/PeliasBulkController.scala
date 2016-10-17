package com.axa.dil.pelias

import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finatra.http.Controller
import com.twitter.util.{Future, Futures}

import scala.collection.JavaConversions._

class PeliasBulkController(peliasHostURL: String) extends Controller() {
  val peliasService = Http.newService(peliasHostURL)
  val api = "/v1/search"

  post(api) {
    addressList: List[Address] =>
      val responseList: List[Future[(Int,Response)]] = addressList.zipWithIndex.map {
        case (address, i)=>
          val req = Request.queryString(api, Map("text" -> address.text, "size" -> address.size.toString))
          peliasService(Request(Method.Get, req)).map(r => (i, r))
      }
      Futures.collect(responseList)
        .map {
        res =>
          response.ok.json(res.sortBy{case (index, response) => index}.map{ case(index, result) => result.getContentString()}.mkString("[", ",", "]"))
      }
  }
}
