package de.ePortfolio.gatling.simulation

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import de.ePortfolio.gatling.conf.HttpConf
import de.ePortfolio.gatling.scenario.SearchGoogle

class GoogleSimulation extends Simulation{

  val searchers = scenario("Searching").exec(SearchGoogle.browse)

  setUp(
    // inject lots of users but throttle request rate
    searchers.inject(atOnceUsers(25), rampUsers(50)over(1 minute)))
      
    // use the default HTTP protocol definition
    .protocols(HttpConf.default)
    
    .maxDuration(5 minutes)

    // verify global statistics
    .assertions(
      // expect mean response time of 500 millis for 99% of the requests
      global.responseTime.percentile2.lessThan(500),
      // expect 99% successful requests
      global.failedRequests.percent.lessThan(2))
}