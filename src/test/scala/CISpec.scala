import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.util.{Success, Try}

class CISpec extends AnyFlatSpec with Matchers{
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark Assignment6")
    .master("local[*]")
    .getOrCreate();

  behavior of "Driver.testing"
  it should "should return hello" in {
    Driver.testing mustEqual "Looks good";
  }

  it should "show success when creating spark session" in {

    Try(spark) should matchPattern {
      case Success(s:SparkSession) =>
    }
  }

}
