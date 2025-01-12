package ASCIIArt.filters.testing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Try}
import ASCIIArt.GrayscaleImage
import ASCIIArt.filters.*

class FiltersSpec extends AnyFlatSpec with Matchers {
  InvertFilterFactory
  RotateFilterFactory
  ScaleFilterFactory
  
  "Filters registry" should "handle filter registration and retrieval" in {
    val testFactory: FilterFactory = new FilterFactory {
      override def apply(args: List[String]): Try[Filter] = null
    }

    Filters.register("test", testFactory)
    Filters.getFilterFactory("test") shouldBe Some(testFactory)
  }

  it should "return registered filter factory" in {
    Filters.getFilterFactory("invert") shouldBe Some(InvertFilterFactory)
    Filters.getFilterFactory("rotate") shouldBe Some(RotateFilterFactory)
    Filters.getFilterFactory("scale") shouldBe Some(ScaleFilterFactory)
  }

  it should "return None for unknown filter" in {
    Filters.getFilterFactory("unknown") shouldBe None
  }

  "InvertFilter" should "invert grayscale values of the image" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = InvertFilterFactory(List()).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(0, 127, 191),
      List(127, 191, 223)
    )
  }

  "InvertFilterFactory" should "fail when arguments are provided" in {
    val result = InvertFilterFactory(List("arg"))
    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "No arguments are required for filter --invert"
  }

  "RotateFilter" should "rotate image by 90 degrees" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = RotateFilterFactory(List("90")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(128, 255),
      List(64, 128),
      List(32, 64)
    )
  }

  it should "rotate image by 270 degrees" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = RotateFilterFactory(List("270")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(64, 32),
      List(128, 64),
      List(255, 128)
    )
  }

  it should "handle negative angles" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = RotateFilterFactory(List("-90")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(64, 32),
      List(128, 64),
      List(255, 128)
    )
  }

  "RotateFilterFactory" should "fail when no arguments provided" in {
    val result = RotateFilterFactory(List())
    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "Exactly one argument is required for filter --rotate"
  }

  it should "fail when non-integer argument provided" in {
    val result = RotateFilterFactory(List("not-a-number"))
    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "Argument for filter --rotate must be an integer"
  }

  "ScaleFilter" should "scale image up by factor of 4" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = ScaleFilterFactory(List("4.0")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(255, 255, 128, 128),
      List(255, 255, 128, 128),
      List(64, 64, 32, 32),
      List(64, 64, 32, 32)
    )
  }

  it should "scale image down by factor of 0.25" in {
    val image = List(
      List(255, 128, 64, 32),
      List(128, 64, 32, 16),
      List(64, 32, 16, 8),
      List(32, 16, 8, 4)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = ScaleFilterFactory(List("0.25")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual List(
      List(143, 36),
      List(36, 9)
    )
  }

  it should "not modify image when scale factor is 0" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = ScaleFilterFactory(List("0.0")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual image
  }

  it should "not modify image when scale factor is too small" in {
    val image = List(
      List(255, 128),
      List(64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val filter = ScaleFilterFactory(List("0.0001")).get

    val result = filter.applyFilter(grayscaleImage).get
    result.pixels shouldEqual image
  }

  "ScaleFilterFactory" should "fail when no arguments provided" in {
    val result = ScaleFilterFactory(List())
    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "Exactly one argument is required for filter --scale"
  }

  it should "fail when non-float argument provided" in {
    val result = ScaleFilterFactory(List("not-a-number"))
    result shouldBe a[Failure[?]]
    result.failed.get.getMessage shouldEqual "Argument for filter --scale must be a float"
  }

  "Filter composition" should "work with multiple filters" in {
    val image = List(
      List(255, 128, 64),
      List(128, 64, 32)
    )
    val grayscaleImage = GrayscaleImage(image).get

    val scaleFilter = ScaleFilterFactory(List("4.0")).get
    val rotateFilter = RotateFilterFactory(List("90")).get
    val invertFilter = InvertFilterFactory(List()).get

    val result = for {
      scaled <- scaleFilter.applyFilter(grayscaleImage)
      rotated <- rotateFilter.applyFilter(scaled)
      inverted <- invertFilter.applyFilter(rotated)
    } yield inverted

    result.get.pixels shouldEqual List(
      List(127, 127, 0, 0),
      List(127, 127, 0, 0),
      List(191, 191, 127, 127),
      List(191, 191, 127, 127),
      List(223, 223, 191, 191),
      List(223, 223, 191, 191)
    )
  }
}