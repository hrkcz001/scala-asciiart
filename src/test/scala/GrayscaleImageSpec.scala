import ASCIIArt.GrayscaleImage
import ASCIIArt.GrayscaleImage.{process, rotate, scale, invert}
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
    result.get.image shouldEqual image
  }

  it should "fail if image rows have different lengths" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64)
    )
    val result = GrayscaleImage(image)

    result shouldBe a[Failure[?]]
    result.failed.get shouldBe an[IllegalArgumentException]
    result.failed.get.getMessage shouldEqual "All rows must have the same length"
  }

  it should "fail if pixel values are out of the range 0-255" in {
    val image = List(
      List(255, 128, 256),
      List(128, -1, 32)
    )
    val result = GrayscaleImage(image)

    result shouldBe a[Failure[?]]
    result.failed.get shouldBe an[IllegalArgumentException]
    result.failed.get.getMessage shouldEqual "All pixels must be between 0 and 255"
  }

  it should "invert the grayscale image" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val inverted = invert(grayscaleImage)

    inverted.inverted shouldBe true
    process(inverted) shouldEqual List(
      List(0, 127, 191),
      List(127, 191, 223)
    )
  }

  it should "rotate the grayscale image by 90 degrees" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val rotated = rotate(90)(grayscaleImage)

    rotated.rotation shouldBe 1
    process(rotated) shouldEqual List(
      List(128, 255),
      List(64, 128),
      List(32, 64)
    )
  }

  it should "rotate the grayscale image by 270 degrees" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val rotated = rotate(270)(grayscaleImage)

    rotated.rotation shouldBe 3
    process(rotated) shouldEqual List(
      List(64, 32),
      List(128, 64),
      List(255, 128)
    )
  }

  it should "scale the grayscale image up by a factor of 4" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val scaled = scale(4.0f)(grayscaleImage)

    scaled.scale shouldBe 4.0f
    process(scaled) shouldEqual List(
      List(255, 255, 128, 128),
      List(255, 255, 128, 128),
      List(64, 64, 32, 32),
      List(64, 64, 32, 32)
    )
  }

  it should "not scale the grayscale image if the factor is 0" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val scaled = scale(0.0f)(grayscaleImage)

    scaled.scale shouldBe 0.0f
    process(scaled) shouldEqual List(
      List(255, 128),
      List(64, 32)
    )
  }

  it should "not scale the grayscale image if the factor is too small" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val scaled = scale(0.0001f)(grayscaleImage)

    scaled.scale shouldBe 0.0001f
    process(scaled) shouldEqual List(
      List(255, 128),
      List(64, 32)
    )
  }

  it should "scale the grayscale image down by a factor of 0.25" in {
    val image = List(
      List(255, 128, 64, 32),
      List(128, 64, 32, 16),
      List(64, 32, 16, 8),
      List(32, 16, 8, 4)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val scaled = scale(0.25f)(grayscaleImage)

    scaled.scale shouldBe 0.25f
    process(scaled) shouldEqual List(
      List(143, 36),
      List(36, 9)
    )
  }

  it should "process the grayscale image with inversion, rotation, and scaling" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val processed = invert(rotate(90)(scale(4.0f)(grayscaleImage)))

    processed.inverted shouldBe true
    processed.rotation shouldBe 1
    processed.scale shouldBe 4.0f
    process(processed) shouldEqual List(
      List(127, 127, 0, 0),
      List(127, 127, 0, 0),
      List(191, 191, 127, 127),
      List(191, 191, 127, 127),
      List(223, 223, 191, 191),
      List(223, 223, 191, 191)
    )
  }
}