package com.typesafe.akka.sample.softrank

import akka.agent.Agent
import akka.actor.{Props, ActorSystem}


case class Job(tweets: Seq[twitter4j.Status], target: Agent[Result])

case class Request(keyword: String, target: Agent[Result])

case class Result(results: Map[String, Long] = Map(), keywords: Set[String] = Set()) {
  def incr(key: String, times: Long = 1): Result = {
    val newVersion = copy(results = results + (key -> (results.get(key).getOrElse(0L) + times)))
    if (newVersion.results.map(_._2).sum >= Softrank.limit) {
      Softrank.poller ! newVersion
    }
    newVersion
  }
}

object Softrank {
  val system = ActorSystem()
  val twitterConnection = system.actorOf(Props[TwitterConnection], "twitter")
  val poller = system.actorOf(Props[AgentPoller], "poller")
  val limit = system.settings.config.getInt("akka.twitter.results-limit")
}


