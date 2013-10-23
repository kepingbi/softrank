

package com.typesafe.akka.sample.softrank.camel

import org.apache.camel.builder.RouteBuilder
import akka.actor.ActorRef
import org.apache.camel.{Exchange, Processor}
import twitter4j.Status
import com.typesafe.akka.sample.softrank.{Result, Job}
import scala.collection.JavaConverters._
import com.typesafe.config.Config
import akka.agent.Agent

class Route(config: Config,
            master: ActorRef) extends RouteBuilder {

  //sends Camel exchanges to Akka
  val processor = new Processor {
    def process(exchange: Exchange) {
      exchange.getIn.getBody match {
        case list: java.util.ArrayList[Status] =>
          val agent = exchange.getIn.getHeader("twitter-agent").asInstanceOf[Agent[Result]]
          master ! Job(list.asScala.toList, agent)

      }
    }
  }
  val numberOfPages = config.getInt("akka.twitter.pages")
  val count = config.getInt("akka.twitter.count")
  val consumerSecret = config.getString("akka.twitter.consumer-secret")
  val consumerKey = config.getString("akka.twitter.consumer-key")
  val token = config.getString("akka.twitter.token")
  val tokenSecret = config.getString("akka.twitter.token-secret")

  def configure() {
    //receives a keyword
    from("direct:twitter")
      //the camel-twitter component enriches the exchange with the results of the search on this keyword
      .to(s"twitter://search?numberOfPages=$numberOfPages&count=$count&consumerKey=$consumerKey&consumerSecret=$consumerSecret&accessToken=$token&accessTokenSecret=$tokenSecret")
      //the results of the search is sent to an Akka actor which stores its results in an agent
      .process(processor)
  }

}
