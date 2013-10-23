

package com.typesafe.akka.sample.softrank

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.specs2.specification.After
import org.specs2.time.NoDurationConversions
import akka.util.Timeout

/* A tiny class that can be used as a Specs2 'context'. */
abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem())
with After
with ImplicitSender
with NoDurationConversions{
  // make sure we shut down the actor system after all tests have run
  def after = system.shutdown()
  implicit val timeout = Timeout(10000)
}