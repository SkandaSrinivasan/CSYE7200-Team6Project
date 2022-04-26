import Streaming.ssc
import com.google.gson.Gson
import org.apache.spark.ml.PipelineModel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j._

import scala.io.Source
import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.streaming._

//import spray.json._
//import DefaultJsonProtocol._

object Try extends App {

  def streaming: Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("Team project")
      .master("local[*]")
      .getOrCreate();
    spark.sparkContext.setLogLevel("ERROR")

    val filters = Source.fromFile("src/main/resources/Filters.txt").getLines.toArray

    import spark.implicits._

    def getTweetsInTimespan(time: Long): DStream[String] = {
      val ssc = new StreamingContext(spark.sparkContext, Seconds(time))

      def getTweets(ssc: StreamingContext) = {
        val tweets: ReceiverInputDStream[Status] =
          TwitterUtils.createStream(ssc, None, filters);
        val tweetsInEnglish = tweets.filter((status: Status) => status.getLang == "en");
        //A stream of tweets
        val geoLocation: DStream[String] = tweetsInEnglish.map((status: Status) => status.getGeoLocation.toString)
        val statuses: DStream[String] = tweetsInEnglish.map((status: Status) => status.getText)
        //Load model
        //System.out.println(statuses)
        val myModel = PipelineModel.load("src/main/resources/model")
        statuses.foreachRDD((rdd: RDD[String]) => {
          val tweetDF: DataFrame = rdd
            .toDF()
            .withColumnRenamed("value", "tweet")
            .withColumn("target", lit(0))

          val cleanedDF = Utils.clean(tweetDF)
          val predictedData: DataFrame = myModel.transform(cleanedDF)

          predictedData.show(false)
        })
        statuses
      }
      val statuses = getTweets(ssc)
      ssc.start
      ssc.awaitTermination

      System.out.println(statuses)
      statuses
    }
    getTweetsInTimespan(10)
  }

  streaming
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