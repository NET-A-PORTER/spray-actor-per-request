package com.netaporter

import akka.io.IO
import spray.can.Http

import akka.actor.{Props, ActorSystem}
import com.netaporter.routing.RestRouting

object Boot extends App {
  implicit val system = ActorSystem("apr-demo")

  val serviceActor = system.actorOf(Props(new RestRouting), name = "rest-routing")

  system.registerOnTermination {
    system.log.info("Actor per request demo shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "localhost", port = 38080)
}