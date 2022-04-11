import Main.{cleaned_df, spark}
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{
  LogisticRegression,
  LogisticRegressionModel
}
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.sql.functions.{length, regexp_replace, udf}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object Model extends App {

  val spark = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer, StringIndexer};
  import org.apache.spark.ml.Pipeline;
  import spark.implicits._;
  spark.sparkContext.setLogLevel("ERROR")

  val trainingDf: DataFrame = spark.read
    .option("header", "false")
    .csv("src/main/resources/training.csv")

  val formatted_df: DataFrame = trainingDf
    .withColumnRenamed("_c0", "target")
    .withColumnRenamed("_c5", "tweet")
    .drop("_c1", "_c2", "_c3", "_c4")

  import org.jsoup.Jsoup
  val decodeHTML = (tweet: String) => {
    Jsoup.parse(tweet).text()
  }

  val decodehtmlUDF = udf(decodeHTML) //UDF using jsoup

  val decodedTweet: DataFrame =
    formatted_df.withColumn(
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

  val splitData: Array[Dataset[Row]] =
    completeDF.randomSplit(Array(0.98, 0.01, 0.01), seed = 2000);

  val trainSet: Dataset[Row] = splitData(0)
  val validationSet: Dataset[Row] = splitData(1)
  val testSet: Dataset[Row] = splitData(2)

  //Prepare pipeline stages
  val tokenize: Tokenizer =
    new Tokenizer().setInputCol("tweet").setOutputCol("words");

  val cv: CountVectorizer =
    new CountVectorizer().setInputCol("words").setOutputCol("cv");

  val idf: IDF =
    new IDF().setInputCol("cv").setOutputCol("features").setMinDocFreq(5)

  val label: StringIndexer =
    new StringIndexer().setInputCol("target").setOutputCol("label")

  val lr: LogisticRegression = new LogisticRegression().setMaxIter(100)

  val pipeline: Pipeline =
    new Pipeline().setStages(Array(tokenize, cv, idf, label, lr));

  val pipelineFit: PipelineModel = pipeline.fit(trainSet);

  val predictions: DataFrame = pipelineFit.transform(validationSet);

  predictions.printSchema()
//  val accuracy = predictions
//    .filter(predictions("label") === predictions("prediction"))
//    .count() / validationSet.count()
//
//  print(s"Accuracy score is: ${accuracy}")
}
