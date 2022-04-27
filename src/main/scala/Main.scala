import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, length, lit, regexp_replace, udf}

class Main {}

object Main extends App {

  val spark = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._;

  val df: DataFrame = spark.read
    .option("header", "true")
    .csv("src/main/resources/vaccination_all_tweets.csv");

  val start_df = df
    .withColumnRenamed("text", "tweet")
    .withColumn("target", lit(0))
    .select("tweet")
    .na
    .drop(Seq("tweet"))

  //Start cleaning
  val completeDF = Utils.clean(start_df);

  val myModel = PipelineModel.load("src/main/resources/model")
  val predictedData: DataFrame = myModel.transform(completeDF)

  val negativeTweets = predictedData
    .filter(predictedData("prediction") =!= 1.0)
    .select($"tweet", $"prediction");

  val positiveTweets = predictedData
    .filter(predictedData("prediction") === 1.0)
    .select($"tweet", $"prediction");

  val positiveCount: Long = positiveTweets.count();
  val negativeCount: Long = negativeTweets.count();

  val totalTweets = positiveTweets.count() + negativeTweets.count();

  println(s"Positive Tweets = ${(positiveCount / totalTweets.toFloat) * 100}%")
  println(s"Negative Tweet  = ${(negativeCount / totalTweets.toFloat) * 100}%")

//  positiveTweets.show()
//  negativeTweets.show()

}
