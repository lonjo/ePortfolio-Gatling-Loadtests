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
    searchers.inject(atOnceUsers(25)))

    .throttle(
      reachRps(50) in (5 seconds),
      holdFor(1 minutes),

      // nonsense here but for more extensive test suits
      jumpToRps(100),
      holdFor(1 minutes),
      jumpToRps(75),
      holdFor(1 hour)) // till the end of the test drive
      
    // use the default HTTP protocol definition
    .protocols(HttpConf.default)

    // verify global statistics
    .assertions(
      // expect mean response time of 500 millis for 99% of the requests
      global.responseTime.percentile2.lessThan(500),
      // expect 99% successful requests (20x,30x)
      global.failedRequests.percent.lessThan(2))
}