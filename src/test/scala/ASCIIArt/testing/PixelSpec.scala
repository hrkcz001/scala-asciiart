package ASCIIArt.testing

import ASCIIArt.Pixel
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PixelSpec extends AnyFlatSpec with Matchers {

  "Pixel.apply" should "return None for invalid RGB values" in {
    Pixel(-1, 100, 100) shouldBe None
    Pixel(256, 0, 0) shouldBe None
    Pixel(100, -10, 100) shouldBe None
  }

  it should "return Some(Pixel) for valid RGB values" in {
    Pixel(0, 0, 0) shouldBe Some(Pixel(0, 0, 0).get)
    Pixel(255, 255, 255) shouldBe Some(Pixel(255, 255, 255).get)
    Pixel(123, 45, 67) shouldBe Some(Pixel(123, 45, 67).get)
  }

  "Pixel.grayscale" should "calculate the correct grayscale value" in {
    val pixel = Pixel(100, 150, 200).get
    Pixel.grayscale(pixel) shouldBe math.round(0.3 * 100 + 0.59 * 150 + 0.11 * 200).toInt
  }

  it should "return 0 for black pixel" in {
    val blackPixel = Pixel(0, 0, 0).get
    Pixel.grayscale(blackPixel) shouldBe 0
  }

  it should "return 255 for white pixel" in {
    val whitePixel = Pixel(255, 255, 255).get
    Pixel.grayscale(whitePixel) shouldBe 255
  }
}