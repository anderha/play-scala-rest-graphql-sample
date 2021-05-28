package todorestgraphqlsample.actorsystem.controllers

import todorestgraphqlsample.actorsystem.actors.commands.{ ResponseQueryHelloWorld, ResponseQueryHelloWorldError }

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import todorestgraphqlsample.actorsystem.actors.commands.{ ResponseQueryHelloWorld, ResponseQueryHelloWorldError }
import todorestgraphqlsample.actorsystem.services.HelloWorldService

import scala.concurrent.ExecutionContext

@Singleton
class ActorHelloWorldController @Inject() (
  cc: ControllerComponents,
  helloWorldService: HelloWorldService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def helloWorldActor(query: String): Action[AnyContent] =
    Action.async { implicit request =>
      val result = for {
        response <- helloWorldService.queryHelloWorld(query)
      } yield response
      result.map {
        case ResponseQueryHelloWorld(_, answer)     => Status(200)(answer)
        case ResponseQueryHelloWorldError(_, error) => Status(400)(error)
      }
    }

}
