package com.netaporter

import akka.actor.ActorSystem
import akka.io.IO
import com.netaporter.routing.RestRouting
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("apr-demo")

  val serviceActor = system.actorOf(RestRouting.props(), name = "rest-routing")

  system.registerOnTermination {
    system.log.info("Actor per request demo shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "localhost", port = 38080)
}