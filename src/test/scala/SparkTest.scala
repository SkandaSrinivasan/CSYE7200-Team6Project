import org.apache.spark.sql.SparkSession
import org.scalatest.matchers.should.Matchers
import org.scalatest.tagobjects.Slow
import org.scalatest.{BeforeAndAfter, flatspec}
import scala.util.Try

class SparkTest extends flatspec.AnyFlatSpec with Matchers with BeforeAndAfter {
  implicit var spark: SparkSession = _

  before {
    spark = SparkSession
      .builder()
      .appName("Team Project")
      .master("local[*]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
  }

  after {
    if (spark != null) {
      spark.stop()
    }
  }
}
