package de.ePortfolio.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.filter.Filter

object SearchGoogle {

  val searchFeeder = sitemap("search.csv").random

  val browse = repeat(10) {
    feed(searchFeeder)
        .exec(http("${searchTerm}").get("/search?q=${searchTerm}"))
  }
}