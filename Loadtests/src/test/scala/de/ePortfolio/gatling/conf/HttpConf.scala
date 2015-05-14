package de.ePortfolio.gatling.conf

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object HttpConf {

  val baseUrl = "http://www.google.de";

  val default = http
    .baseURL(baseUrl)
    .warmUp(baseUrl)
    .disableFollowRedirect
    .inferHtmlResources()
}