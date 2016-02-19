package com.netaporter.routing

import akka.actor.Props
import com.netaporter._
import com.netaporter.clients.{OwnerClient, PetClient}
import com.netaporter.core.GetPetsWithOwnersActor
import spray.routing.{HttpServiceActor, Route}

object RestRouting {
  def props(): Props = Props(new RestRouting)
}

class RestRouting extends HttpServiceActor with PerRequestCreator {

  def receive = runRoute(route)

  val petService = context.actorOf(Props[PetClient])
  val ownerService = context.actorOf(Props[OwnerClient])

  val route = {
    get {
      path("pets") {
        parameters('names) { names =>
          petsWithOwner {
            GetPetsWithOwners(names.split(',').toList)
          }
        }
      }
    }
  }

  def petsWithOwner(message : RestMessage): Route =
    ctx => perRequest(ctx, Props(new GetPetsWithOwnersActor(petService, ownerService)), message)
}
