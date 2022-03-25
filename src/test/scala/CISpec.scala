import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class CISpec extends AnyFlatSpec with Matchers{

  behavior of "Driver.testing"
  it should "should return hello" in {
    Driver.testing mustEqual "Looks good";
  }
}
