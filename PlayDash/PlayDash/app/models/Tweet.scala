package models

import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._

case class Tweet(tweet: String, sentiment: String)

object Tweet {

  implicit val fmt: Format[Tweet] = Json.format[Tweet]

  implicit object TweetBSONReader extends BSONDocumentReader[Tweet] {
    def read(doc: BSONDocument): Tweet = {
      Tweet(
        doc.getAs[String]("tweet").get,
        doc.getAs[String]("sentiment").get
      )
    }
  }

  implicit object TweetBSONWriter extends BSONDocumentWriter[Tweet] {
    def write(t: Tweet): BSONDocument = {
      BSONDocument(
        "title" -> t.tweet,
        "sentiment" -> t.sentiment
      )
    }
  }
}
