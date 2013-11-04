package com.netaporter.routing

import akka.actor.{Props, Actor}
import com.netaporter._
import spray.routing.{Route, HttpService}
import com.netaporter.core.GetPetsWithOwnersActor
import com.netaporter.clients.{OwnerClient, PetClient}

class RestRouting extends HttpService with Actor with PerRequestCreator {

  implicit def actorRefFactory = context

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
