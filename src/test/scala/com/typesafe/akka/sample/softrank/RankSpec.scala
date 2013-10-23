

package com.typesafe.akka.sample.softrank

import org.specs2.mutable.SpecificationWithJUnit
import akka.agent.Agent
import akka.pattern.ask
import scala.concurrent.Await
import com.typesafe.akka.sample.softrank.Softrank
import com.typesafe.akka.sample.softrank.Result

class RankSpec extends SpecificationWithJUnit {

  "Ranking based on the default keyword should produce at least 10 different twittos" in new AkkaTestkitSpecs2Support {
    val keyword = system.settings.config.getString("akka.twitter.keyword")
    val twitter = Softrank.twitterConnection
    val agent = Await.result((twitter ? keyword).mapTo[Agent[Result]], timeout.duration)

    val result = agent.get()
    println("#" * 25)
    println(result.results.map(_._2).sum + " results for " + result.keywords.mkString(","))
    result.results.toList.sortBy(_._2).reverse.take(10).foreach(x => println(x._1 + "->" + x._2))
    println("...")
    println("#" * 25)
    agent.get().results.size should be_>=(10)
  }

}





