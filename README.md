# ePortfolio-Gatling-Loadtests

## Preparation 
* Install Eclipse
* Go to the Marcetplace and install Scala IDE

## Create Maven Project
First create a new Maven Project and then configure it to work with Skala.
You do that with a rightclick on the Project and then select "configure" and there choose "add Scala Nature"

## Add dependencies and  Plugin to pom
Now you can add the Gatling dependencie [from here](http://gatling.io/#/download) to your pom.
To install the Gatling Maven Plugin you can follow the instructions [here](http://gatling.io/docs/2.1.6/extensions/maven_plugin.html).
You should add the plugin with the default configuration and the default execution settings.

You will need this plugin to execute the Gatling tests during the Maven tests phase.

## Create HTTP configuration
Next create the http configuration so Gatling knows how and where to send its http requests.
For mor information look at the [documentation](http://gatling.io/docs/2.1.6/http/http_protocol.html).

Example:
    object HttpConf {

    val baseUrl = "http://www.google.de";

    val default = http
      .baseURL(baseUrl)
      .warmUp(baseUrl)
      .disableFollowRedirect
      .inferHtmlResources()
    }

## Create a scenario
A scenario is used to execute http request of a specific type.
There is a nice [Gatling Cheat Sheet](http://gatling.io/docs/2.1.6/cheat-sheet.html) wehre you can look up all the possible components to build an Scenario.
Or the [Documentation](http://gatling.io/docs/2.1.6/http/http_request.html).

Example:
    object SearchGoogle {
    
    val searchFeeder = csv("search.csv").random //feeder to randomly choose a search text from a csv file
    
    val browse = repeat(10) {
      feed(searchFeeder)
          .exec(http("${searchTerm}").get("/search?q=${searchTerm}"))
      }
    }

## Put everything together in a simulation
The simulation puts all the Elements together and generates the users.
[Documentation](http://gatling.io/docs/2.1.6/http/http_request.html)
[Gatling Cheat Sheet](http://gatling.io/docs/2.1.6/cheat-sheet.html)

Example:
    class GoogleSimulation extends Simulation{
  
    val searchers = scenario("Searching").exec(SearchGoogle.browse)
  
    setUp(
      // inject lots of users but throttle request rate
      searchers.inject(atOnceUsers(25), rampUsers(50)over(1 minute)))
        
      // use the default HTTP protocol definition
      .protocols(HttpConf.default)
      
      .maxDuration(5 minutes) //maximum duration of the Simulation
  
      // verify global statistics
      .assertions(
        // expect mean response time of 500 millis for 99% of the requests
        global.responseTime.percentile2.lessThan(500),
        // expect 99% successful requests
        global.failedRequests.percent.lessThan(2))
    }

## Setup a run configuration
To start the simulation you have to create a new Run Configuration:
* Goals: "gatling:execute"

## the Report
When the simulation is run through you can find your simulation in the target folder under target/gatling/results
