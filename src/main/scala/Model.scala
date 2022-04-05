import Main.spark
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object Model extends App {

  val spark = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  spark.sparkContext.setLogLevel("ERROR")

  val trainingDf: DataFrame = spark.read
    .option("header", "false")
    .csv("src/main/resources/training.csv")

  trainingDf
    .withColumnRenamed("_c0", "sentiment")
    .withColumnRenamed("_c5", "tweet")
    .drop("_c1", "_c2", "_c3", "_c4")
    .show();
  val splitData: Array[Dataset[Row]] =
    trainingDf.randomSplit(Array(0.98, 0.01, 0.01), seed = 2000);

  println(splitData.length)
  val trainSet: Dataset[Row] = splitData(0)
  val validationSet: Dataset[Row] = splitData(1)
  val testSet: Dataset[Row] = splitData(2)

  println(trainSet.count())
}
