

package com.typesafe.akka.sample.softrank

import akka.actor.{ActorLogging, Actor}
import akka.util.Timeout

object App {

  def main(args: Array[String]) {
    val system = Softrank.system
    implicit val timeout = Timeout(1000)

    val keyword = system.settings.config.getString("akka.twitter.keyword")

    val twitter = Softrank.twitterConnection

    twitter ! keyword

  }
}

/**
 * Actor that polls the Agent to find its result
 */
class AgentPoller extends Actor with ActorLogging {

  def receive: Actor.Receive = {

    case result: Result =>
      context.system.shutdown()
      printResult(result)
  }

  def printResult(result: Result) {
    println("#" * 25)
    println(result.results.map(_._2).sum + " results for " + result.keywords.mkString(","))
    result.results.toList.sortBy(_._2).reverse.take(10).foreach(x => println(x._1 + "->" + x._2))
    println("...")
    println("#" * 25)
  }
}
