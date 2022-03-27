import org.apache.spark.sql.SparkSession

class Main {

}

object Main extends App {
  val spark = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  spark.sparkContext.setLogLevel("ERROR")

    val df = spark.read
      .option("header", "true")
      .csv("src/main/resources/vaccination_all_tweets.csv");

    println(df.count())
}