import org.apache.spark.ml.PipelineModel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j._

import scala.io.Source
import org.apache.spark.{status, _}
import org.apache.spark.rdd._
import org.apache.spark.streaming._

//import spray.json._
//import DefaultJsonProtocol._
import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.bson.BsonObjectId
import Helpers._;
object Streaming extends App {
  import org.mongodb.scala._
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Team project")
    .master("local[*]")
    .getOrCreate();
  spark.sparkContext.setLogLevel("ERROR")

  val filters =
    Source.fromFile("src/main/resources/Filters.txt").getLines.toArray

  import spark.implicits._

  val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

  val tweets: ReceiverInputDStream[Status] =
    TwitterUtils.createStream(ssc, None, filters);
  val tweetsInEnglish: DStream[Status] =
    tweets.filter((status: Status) => status.getLang == "en");
  //A stream of tweets
  val statuses: DStream[StatusModel] =
    tweetsInEnglish.map((status: Status) => {
      status.getPlace match {
        case null => StatusModel(status.getText, null)
        case _    => StatusModel(status.getText, Some(status.getPlace.toString))
      }
    })

  //Load model
  val mongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("Test")
  val coll: MongoCollection[Document] = database.getCollection("tweets")

  val myModel = PipelineModel.load("src/main/resources/model")
  statuses.foreachRDD((rdd: RDD[StatusModel]) => {
    val tweetDF: DataFrame = rdd
      .toDF()
      .withColumn("target", lit(0))
      .withColumn("origTweet", $"tweet")
    val cleanedDF = Utils.clean(tweetDF)
    val predictedData: DataFrame = myModel.transform(cleanedDF)
    val finalDF: DataFrame =
      predictedData.select($"origTweet", $"geoLocation", $"prediction")
    finalDF.foreach((row: Row) => {
      val document: Document = Document(
        "tweet" -> row.get(0).toString,
        "sentiment" -> row.get(2).toString
      )
      coll.insertOne(document).printResults()
    }): Unit

  })
  ssc.start()
  ssc.awaitTermination()

}
/*
#Covid19
#Covid-19
#Coronavirus
#StayHomeStaySafe
#StayHome
#QuarantineandChill
#LockdownNow
#Covidiots
#MyPandemicSurvivalPlan
#FlattenTheCurve
#SocialDistancing
#TogetherAtHome
#BigOnlinePar

 */
