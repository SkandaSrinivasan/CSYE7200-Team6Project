package repositories

import models.Tweet
import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}

@Singleton
class TweetRepository @Inject() (implicit
    executionContext: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  def collection: Future[BSONCollection] =
    reactiveMongoApi.database.map(db => db.collection("tweets"))

  def findAll(limit: Int = 100): Future[Seq[Tweet]] = {
    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Tweet])
        .cursor[Tweet](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Tweet]]())
    )
  }
}
