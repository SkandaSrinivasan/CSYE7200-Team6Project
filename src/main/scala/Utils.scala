import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{regexp_replace, udf, length, filter};
object Utils {

  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  import spark.implicits._;

  def clean(start_df: DataFrame): DataFrame = {
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

    val cleanTweet: DataFrame =
      decodedTweet.withColumn(
        "tweet",
        regexp_replace(
          $"tweet",
          "@[A-Za-z0-9_]+|https?://[^ ]+|www.[^ ]+",
          ""
        ) //Lets strip out all mentions(@) and remove all urls
      )

    val utfCleanedTweet: DataFrame = cleanTweet.withColumn(
      "tweet",
      regexp_replace(
        $"tweet",
        "\\ufffd",
        "?"
      )
    ) //Remove the weird diamond qmark from non unicode chars and replace them with plain ol ?

    utfCleanedTweet.na
      .drop(Seq("tweet"))
      .filter(
        length($"tweet") > 1
      ) //Remove tweets that are empty after cleaning

  }
}
