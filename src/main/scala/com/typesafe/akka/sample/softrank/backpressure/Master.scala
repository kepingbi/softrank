package com.typesafe.akka.sample.softrank.backpressure

import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import com.typesafe.akka.sample.softrank.Job

class Master extends Actor with ActorLogging {

  var sleepingWorkers = Set[ActorRef]()
  var waittingJobs = Seq[Job]()

  val batchSize = context.system.settings.config.getInt("akka.twitter.batch")
  //initialize its worker
  for (a <- 1 to context.system.settings.config.getInt("akka.twitter.workers")) {
    //add a new worker to the master
    self ! NewWorker(context.actorOf(Props[Worker]))
  }

  def receive: Actor.Receive = {

    case job: Job if (sleepingWorkers.isEmpty) =>
      waittingJobs +:= job

    //case job: Job if (!job.tweets.isEmpty) =>  //with protective programming
    case job: Job => //without protective programming
      sendBatchJob(job, sleepingWorkers.head)
      sleepingWorkers = sleepingWorkers.tail

    case NewWorker(worker) if (waittingJobs.isEmpty) =>
      sleepingWorkers += worker

    case NewWorker(worker) =>
      sendBatchJob(waittingJobs.head, worker)
      waittingJobs = waittingJobs.tail
  }

  protected def sendBatchJob(job: Job, worker: ActorRef) {
    //split to the batch size
    val chunks = job.tweets.grouped(batchSize).toSeq
    worker ! job.copy(tweets = chunks.head)
    chunks.tail.foreach(x => self ! job.copy(tweets = x))
  }

  override def preRestart(failure: Throwable, msg: Option[Any]) {
    msg match {
      case Some(Job(Seq(), _)) =>
        log.info("tried to call the head method on an empty Seq")

      case other =>
        log.info("unknown error -> escalating")
        context.parent ! failure
    }
    sleepingWorkers.foreach(self ! NewWorker(_))
    waittingJobs.foreach(self ! _)
  }
}


