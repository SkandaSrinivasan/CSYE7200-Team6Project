import org.apache.spark.ml.PipelineModel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status

import scala.io.Source

import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.streaming._


object Streaming extends App {




  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  spark.sparkContext.setLogLevel("ERROR")
  val ssc = new StreamingContext(spark.sparkContext, Seconds(10))
  import spark.implicits._
  val filters = Source.fromFile("src/main/resources/Filters.txt").getLines.toArray
  val tweets: ReceiverInputDStream[Status] =
    TwitterUtils.createStream(ssc, None, filters);
  //A stream of tweets
  val statuses: DStream[String] = tweets.filter((status: Status) => status.getLang == "en").map((status: Status) => status.getText)
  //Load model
  val myModel = PipelineModel.load("src/main/resources/model")
  statuses.foreachRDD((rdd: RDD[String]) => {
    val tweetDF: DataFrame = rdd
      .toDF()
      .withColumnRenamed("value", "tweet")
      .withColumn("target", lit(0))

    val cleanedDF = Utils.clean(tweetDF)
    val predictedData: DataFrame = myModel.transform(cleanedDF)

    predictedData.show()
  })
  ssc.start
  ssc.awaitTermination
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