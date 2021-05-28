package de.innfactory.todorestgraphqlsample.websockets.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import de.innfactory.todorestgraphqlsample.actorsystem.services.HelloWorldService
import de.innfactory.todorestgraphqlsample.websockets.actors.WebSocketActor

import javax.inject.{ Inject, Singleton }
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class WebsocketController @Inject() (
  cc: ControllerComponents
)(implicit ec: ExecutionContext, implicit val system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  def socket =
    WebSocket.accept[String, String] { request =>
      ActorFlow.actorRef { out =>
        WebSocketActor.props(out)
      }
    }

}
