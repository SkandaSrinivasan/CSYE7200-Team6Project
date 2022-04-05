import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, length}

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

  val cleaned_df: DataFrame =
    df.dropDuplicates(Seq("text"))
      .na
      .drop(Seq("text"))
      .filter(
        length($"text") > 4
      ) //Tweets that exceed atleast 3 characters seems reasonable

  //Lets convert our Dataframe to a Dataset now
  val tweets: Dataset[Tweet] = df.select("text", "user_location").as[Tweet]
  tweets
    .filter(tweet => tweet.text != null && tweet.user_location != null)
    .show()
}
