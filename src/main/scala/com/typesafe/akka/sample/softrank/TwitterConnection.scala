

package com.typesafe.akka.sample.softrank

import akka.actor.{ActorLogging, Actor}
import akka.agent.Agent
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.akka.sample.softrank.camel.TwitterAware

class TwitterConnection extends Actor with TwitterAware with ActorLogging {

  def receive: Receive = {
    case keyword: String =>
      val agent = Agent(Result(keywords = Set(keyword)))
      sender ! agent
      rank(keyword, agent)
      context.become(alreadyRanking(Set(keyword), agent))
  }

  def alreadyRanking(keywords: Set[String], agent: Agent[Result]): Receive = {
    case keyword: String =>
      sender ! agent

      if (!keywords.contains(keyword) && keywords.size <= searchLimit) {
        agent.alter(_.copy(keywords = keywords + keyword))
        rank(keyword, agent)
        context.become(alreadyRanking(keywords + keyword, agent))
      }

    case throwable: Throwable =>
      log.warning("sending jobs containing empty list of tweets")
  }

}
