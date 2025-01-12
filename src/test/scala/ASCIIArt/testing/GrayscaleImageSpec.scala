package ASCIIArt.testing

import ASCIIArt.GrayscaleImage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class GrayscaleImageSpec extends AnyFlatSpec with Matchers {

  "GrayscaleImage" should "be created successfully with valid image data" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val result = GrayscaleImage(image)

    result shouldBe a[Success[?]]
    result.get.pixels shouldEqual image
  }

  it should "fail if image rows have different lengths" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64)
    )
    val result = GrayscaleImage(image)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "All rows must have the same length"
  }

  it should "fail if pixel values are out of the range 0-255" in {
    val image = List(
      List(255, 128, 256),
      List(128, -1, 32)
    )
    val result = GrayscaleImage(image)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "All pixels must be between 0 and 255"
  }
}