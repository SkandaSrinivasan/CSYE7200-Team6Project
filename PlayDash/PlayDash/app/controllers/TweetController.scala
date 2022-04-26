package controllers

import javax.inject._
import play.api.mvc._
import repositories.TweetRepository
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}
import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}

import models.Tweet
import play.api.libs.json.JsValue

@Singleton
class TweetController @Inject() (implicit
    executionContext: ExecutionContext,
    val tweetRepository: TweetRepository,
    val controllerComponents: ControllerComponents
) extends BaseController {
  def findAll(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      tweetRepository.findAll().map { tweets =>
        Ok(Json.toJson(tweets))
      }
  }

}
