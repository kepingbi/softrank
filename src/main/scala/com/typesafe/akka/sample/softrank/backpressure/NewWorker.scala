

package com.typesafe.akka.sample.softrank.backpressure

import akka.actor.ActorRef

/**
 * Message that announce a fresh worker to the master
 * @param worker
 */
case class NewWorker(worker: ActorRef)
