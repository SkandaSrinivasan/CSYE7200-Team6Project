import Main.start_df
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.util.{Success, Try}

class CISpec extends AnyFlatSpec with Matchers {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  behavior of "Spark session"
  it should "create a valid spark session and create a empty DF" in {
    noException should be thrownBy spark.emptyDataFrame
  }
  val df: DataFrame = spark.read
    .option("header", "true")
    .csv("src/main/resources/vaccination_all_tweets.csv");

  behavior of "Target Dataframe"
  it should "return a row count of 541728" in {
    assert(df.count() == 541728)
  }
  val transformed_df: DataFrame = df
    .withColumnRenamed("text", "tweet")
    .withColumn("target", lit(0))
    .select("tweet")
    .na
    .drop(Seq("tweet"))

  behavior of "Transformations and Cleaning"
  it should "return a row count of 188332 after removing duplicate tweets" in {
    assert(transformed_df.count() == 188332)
  }
  val cleaned_df: DataFrame = Utils.clean(transformed_df);
  it should "return a row count of 172861 after cleaning the dataframe" in {
    assert(cleaned_df.count() == 172861)
  }

}
