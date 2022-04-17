import org.apache.spark.ml.PipelineModel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status

object Streaming extends App {
  import org.apache.spark.streaming.twitter._
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();
  import spark.implicits._;
  spark.sparkContext.setLogLevel("ERROR")
  val ssc = new StreamingContext(spark.sparkContext, Seconds(60))
  val tweets: ReceiverInputDStream[Status] =
    TwitterUtils.createStream(ssc, None);
  //A stream of tweets
  val statuses: DStream[String] = tweets.map((status: Status) => status.getText)
  //Load model
  val myModel = PipelineModel.load("src/main/resources/model")
  statuses.foreachRDD((rdd: RDD[String]) => {
    val tweetDF: DataFrame = rdd
      .toDF()
      .withColumnRenamed("value", "tweet")
      .withColumn("target", lit(0))

    val predictedData: DataFrame = myModel.transform(tweetDF)
    predictedData.show()
  })
  ssc.start()
  ssc.awaitTermination()
}
