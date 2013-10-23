

package com.typesafe.akka.sample.softrank.backpressure

import akka.actor.Actor
import com.typesafe.akka.sample.softrank.Job
import com.typesafe.akka.sample.softrank.Softrank

class Worker extends Actor {

  val twitter = Softrank.twitterConnection

  def receive: Actor.Receive = {
    case job: Job =>
      job.tweets.foreach(x => {
        if (!x.isRetweet) {
          job.target.alter(y => y.incr(x.getUser.getName, x.getRetweetCount + 1L))
          findHashTags(x.getText).foreach(h => {
            twitter ! h
          })
        }
      })
      sender ! NewWorker(self)
  }

  def findHashTags(tweet: String): List[String] = {
    tweet.split(" ").filter(_.startsWith("#")).map(_.substring(1)).toList
  }
}
