package com.typesafe.akka.sample.softrank.camel

import org.apache.camel.impl.{DefaultExchange, DefaultCamelContext}
import akka.actor.{Actor, Props}
import com.typesafe.akka.sample.softrank.Result
import akka.agent.Agent
import org.apache.camel.component.twitter.TwitterComponent
import com.typesafe.akka.sample.softrank.backpressure.Master

trait TwitterAware {
  self: Actor =>

  //maximal number of searched keywords
  protected val searchLimit = context.system.settings.config.getInt("akka.twitter.keywords-limit")

  //actor which manages the requests
  private val master = context.actorOf(Props[Master], "master")

  //prepare camel in order to manage the requests
  private val cc = new DefaultCamelContext()
  private val producer = cc.createProducerTemplate()
  cc.addComponent("twitter", new TwitterComponent())
  cc.addRoutes(new Route(context.system.settings.config, master))
  cc.start()

  //send the keyword to the route
  protected def rank(keyword: String, agent: Agent[Result]) {
    val exchange = new DefaultExchange(cc)
    exchange.getIn.setHeader("CamelTwitterKeywords", keyword)
    exchange.getIn.setHeader("twitter-agent", agent)
    producer.send("direct:twitter", exchange)
  }

}
