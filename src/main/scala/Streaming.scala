import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status

object Streaming extends App {
  import org.apache.spark.streaming.twitter._
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();
  spark.sparkContext.setLogLevel("ERROR")
  val ssc = new StreamingContext(spark.sparkContext, Seconds(60))
  val tweets: ReceiverInputDStream[Status] =
    TwitterUtils.createStream(ssc, None);
  val statuses = tweets.map(status => status.getText)
  statuses.print()

  ssc.start()
  ssc.awaitTermination()
}
