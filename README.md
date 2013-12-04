# Spray Actor Per Request

This project provides an example spray application that uses the Actor per request model.

Why would you want to spin up an Actor for each HTTP request?

 * Easily manage a tree of request scoped Actors in the application core
  * The per request actor can clean them up in the event of a timeouts and failures
  * Leverage the actor supervision hierarchy to propogate failures up to the RequestContext, so you can return useful error responses
 * Promote `Tell, Don't Ask`
  * Using request scoped Actors in the application core can make it easier to use tell (`!`) over ask (`?`)

Resources:

 * Scala Exchange Presentation ([video](http://skillsmatter.com/podcast/scala/scala-does-the-catwalk))
 * Mathias describes the actor per request approach against others.
   ([mailing list](https://groups.google.com/forum/#!msg/spray-user/5x9kba7j1FI/r_aaDTPWHFkJ))

## Example App

### Overview

This example application provides an API to get a list of pets with their owners. There are already two services that
provide a list of pets and a list of animals and the responsibility of this application is to simply aggregate these two
together.

Our application is made up of three modules:

 * **Application Core** - The `core` module contains the business logic for our application. In this example this is
   how we aggregate pets with their owners.
 * **Routing** - The `routing` module contains our spray routing which describes our RESTful endpoints. It also contains
   our `PerRequest` actor which bridges the gap between the `routing` and the `core` modules and contains the piece of
   code this example project aims to demonstrate.
 * **Clients** - Our code to consume two existing services that provide us with a list of pets and a list of owners.
   These services could be databases, RESTful APIs, etc. It doesn't really matter for the purposes of this example.

Ideally modules these would be in separate sub-projects to prevent unnecessary compile time dependencies, however for simplicity
purposes they are just kept in separate packages in this example.

### Running

    sbt run

### Successful request

If we request the pet Lassie:

    GET http://localhost:38080/pets?names=Lassie

We get a successful response:

    {
      "pets": [
        {
          "name": "Lassie",
          "owner": {
            "name": "Jeff Morrow"
          }
        }
      ]
    }

In this scenario, any request scoped actors in the application core are stopped by the `PerRequest` actor.

### Request Timeouts

Tortoises are slow. If we request a tortoise the `PetClient` will not reply to our application core quick enough. The
timeout of 2 seconds in our `PerRequest` actor will happen first.

    GET http://localhost:38080/pets?names=Tortoise

    {
      "message": "Request timeout"
    }

In this scenario, any request scoped actors in the application core are stopped by the `PerRequest` actor.

### Validation

You shouldn't keep a Lion as a pet. Quite frankly they are too dangerous.

    GET http://localhost:38080/pets?names=Lion

    {
      "message": "Lions are too dangerous!"
    }

In this scenario, our application core returns a generic `Validation` message to our `PerRequest` actor to complete. Any
request scoped actors in the application core are stopped by the `PerRequest` actor.

### Failures

What about unexpected failures? There is a "bug" in our application core that throws a `PetOverflowException` if we
request too many pets:

    GET http://localhost:38080/pets?names=Lassie,Tweety,Tom

    {
      "message": "PetOverflowException: OMG. Pets. Everywhere."
    }

Any failures that not handled by the application core can be escalated up to the supervision strategy in our
`PerRequest` actor. The `PerRequest` actor is too generic to recover from any business logic failures, so it will
simply handle all failures by completing the request with an error response. Any request scoped actors in the
application core are stopped by the `PerRequest` actor.
