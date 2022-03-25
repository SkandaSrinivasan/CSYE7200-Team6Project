import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class CISpec extends AnyFlatSpec with Matchers{

  behavior of "Project"
  it should "Driver.testing should return hello" in {
    Driver.testing mustEqual "Looks good";
  }
}
