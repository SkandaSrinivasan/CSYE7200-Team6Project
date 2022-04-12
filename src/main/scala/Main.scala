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
  import org.jsoup.Jsoup
  val decodeHTML = (tweet: String) => {
    Jsoup.parse(tweet).text()
  }
  val decodehtmlUDF = udf(decodeHTML) //UDF using jsoup

  val decodedTweet: DataFrame =
    start_df.withColumn(
      "tweet",
      decodehtmlUDF($"tweet")
    ) // Parse all the html stuff like &amp &quot ,etc

  val cleanTweet: DataFrame = {
    decodedTweet.withColumn(
      "tweet",
      regexp_replace(
        $"tweet",
        "@[A-Za-z0-9_]+|https?://[^ ]+|www.[^ ]+",
        ""
      ) //Lets strip out all mentions(@) and remove all urls
    )
  }

  val utfCleanedTweet = cleanTweet.withColumn(
    "tweet",
    regexp_replace(
      $"tweet",
      "\\ufffd",
      "?"
    )
  ) //Remove the weird diamond qmark from non unicode chars and replace them with plain ol ?

  val completeDF =
    utfCleanedTweet.na.drop(Seq("tweet")).filter(length($"tweet") > 1)

  val myModel = PipelineModel.load("src/main/resources/model")
  val predictedData: DataFrame = myModel.transform(completeDF)

  val negativeTweets = predictedData
    .filter(predictedData("prediction") =!= 1.0)
    .select($"tweet", $"prediction");

  val postiveTweets = predictedData
    .filter(predictedData("prediction") === 1.0)
    .select($"tweet", $"prediction");

  println(postiveTweets.count())
  println(negativeTweets.count())

  postiveTweets.show()
  negativeTweets.show()
}
