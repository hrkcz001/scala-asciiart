import ASCIIArt.{Filter, GrayscaleImage}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

val EXAMPLE_IMAGE: List[List[Int]] = List(
  List(255, 128, 64),
  List(128, 64, 32)
)

class FilterSpec extends AnyFlatSpec with Matchers {

  "Filter.argumentsRequired" should "return the correct number of arguments for valid filters" in {
    Filter.argumentsRequired("--invert") shouldEqual Some(0)
    Filter.argumentsRequired("--rotate") shouldEqual Some(1)
    Filter.argumentsRequired("--scale") shouldEqual Some(1)
  }

  it should "return None for invalid filters" in {
    Filter.argumentsRequired("--unknown") shouldEqual None
    Filter.argumentsRequired("--random") shouldEqual None
  }

  "Filter.applyFilter" should "apply the invert filter successfully" in {
    val image = GrayscaleImage(EXAMPLE_IMAGE).get

    val filter = Filter("--invert", List())
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Success[?]]
    result.get.inverted shouldBe true
  }

  it should "apply the rotate filter successfully with a valid integer argument" in {
    val image = GrayscaleImage(EXAMPLE_IMAGE).get

    val filter = Filter("--rotate", List("90"))
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Success[?]]
    result.get.rotation shouldBe 1
  }

  it should "fail to apply the rotate filter with an invalid integer argument" in {
    val image = GrayscaleImage(EXAMPLE_IMAGE).get

    val filter = Filter("--rotate", List("notAnInteger"))
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Failure[?]]
    result.failed.get shouldBe an[IllegalArgumentException]
    result.failed.get.getMessage shouldEqual "Int argument(s) required for filter --rotate"
  }

  it should "apply the scale filter successfully with a valid float argument" in {
    val image = GrayscaleImage(EXAMPLE_IMAGE).get

    val filter = Filter("--scale", List("4.0"))
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Success[?]]
    result.get.scale shouldBe 4.0f
  }

  it should "fail to apply the scale filter with an invalid float argument" in {
    val image = GrayscaleImage(EXAMPLE_IMAGE).get

    val filter = Filter("--scale", List("notAFloat"))
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Failure[?]]
    result.failed.get shouldBe an[IllegalArgumentException]
    result.failed.get.getMessage shouldEqual "Float argument(s) required for filter --scale"
  }

  it should "return a failure when applying an unknown filter" in {
    val image = GrayscaleImage(List(
      List(255, 128, 64),
      List(128, 64, 32)
    )).get

    val filter = Filter("--unknown", List())
    val result = Filter.applyFilter(image, filter)

    result shouldBe a[Failure[?]]
    result.failed.get shouldBe an[IllegalArgumentException]
    result.failed.get.getMessage shouldEqual "Unknown filter --unknown"
  }
}