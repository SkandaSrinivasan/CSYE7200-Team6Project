import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{
  GBTClassifier,
  LinearSVC,
  LogisticRegression,
  LogisticRegressionModel,
  RandomForestClassifier
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

  //Load dataframe from local csv file
  val trainingDf: DataFrame = spark.read
    .option("header", "false")
    .csv("src/main/resources/training.csv")

  //Rename columns and drop unnecessary columns
  val formatted_df: DataFrame = trainingDf
    .withColumnRenamed("_c0", "target")
    .withColumnRenamed("_c5", "tweet")
    .withColumn("origTweet", $"tweet")
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

  //Drop na values and remove tweets which contain 1 or less token
  val completeDF =
    utfCleanedTweet.na.drop(Seq("tweet")).filter(length($"tweet") > 1)

  //Split data into train set, validation set and test set
  val splitData: Array[Dataset[Row]] =
    completeDF.randomSplit(Array(0.98, 0.01, 0.01), seed = 2000);

  val trainSet: Dataset[Row] = splitData(0)
  val validationSet: Dataset[Row] = splitData(1)
  val testSet: Dataset[Row] = splitData(2)

  //Prepare pipeline stages
  val tokenize: Tokenizer =
    new Tokenizer().setInputCol("tweet").setOutputCol("words");

  //Convert a collection of text documents to vectors of token counts
  val cv: CountVectorizer =
    new CountVectorizer().setInputCol("words").setOutputCol("cv");

  //Down-weights features which appear frequently in a corpus
  val idf: IDF =
    new IDF().setInputCol("cv").setOutputCol("features").setMinDocFreq(5)

  //Encodes a string column of labels to a column of label indices
  val label: StringIndexer =
    new StringIndexer().setInputCol("target").setOutputCol("label")

  //Linear regression model
  val lr: LogisticRegression = new LogisticRegression().setMaxIter(100)

  //Linear support vector classifier
  val lsvc = new LinearSVC().setMaxIter(100).setRegParam(0.1)

  //Gradient boosted tree classifier
  val gbt = new GBTClassifier()
    .setLabelCol("label")
    .setFeaturesCol("features")
    .setMaxIter(100)
    .setFeatureSubsetStrategy("auto")

  //Random forest classifier
  val rf = new RandomForestClassifier()
    .setLabelCol("label")
    .setFeaturesCol("features")
    .setNumTrees(20)

  //Combine multiple algorithms into a single pipeline
  val pipeline: Pipeline =
    new Pipeline().setStages(Array(tokenize, cv, idf, label, lsvc));

  //Fit the model on the train set
  val pipelineFit: PipelineModel = pipeline.fit(trainSet);

  //Make predictions on the validation set using the pipeline
  val predictions: DataFrame = pipelineFit.transform(validationSet);

  //Count the number of correct predictions
  val accurateCount = predictions
    .filter(predictions("label") === predictions("prediction"))
    .count()
  //Get the total number of data being tested
  val totalCount = validationSet.count();

  print(s"Accuracy score is: ${(accurateCount / totalCount.toFloat) * 100} %")

  //Write the pipeline model to local disk
  pipelineFit.write
    .overwrite()
    .save(
      "src/main/resources/model"
    ) //Saving the trained and fitted model to use with primary dataset
}
