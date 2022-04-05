import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, length, regexp_replace}

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

  //Tweets that exceed atleast 3 characters seems reasonable
  val cleaned_df: DataFrame =
    df.select("text")
      .dropDuplicates(Seq("text"))
      .na
      .drop(Seq("text"))
      .filter(
        length($"text") > 3
      )
  //Let's do some real cleaning now.
  val m_df =
    cleaned_df.withColumn(
      "text",
      regexp_replace(
        $"text",
        "@[A-Za-z0-9_]+|https?://[^ ]+",
        ""
      ) //Lets strip out all mentions(@) and remove all urls
    )
  m_df.show()
  println(m_df.count())
}
