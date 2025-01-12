package ASCIIArt.converters.testing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ASCIIArt.GrayscaleImage
import ASCIIArt.converters._

class ASCIIConvertersSpec extends AnyFlatSpec with Matchers {
  DefaultASCIIConverterFactory
  ExtendedASCIIConverterFactory
  NonlinearASCIIConverterFactory
  
  "ASCIIConverters registry" should "handle converter registration and retrieval" in {
    val testFactory: ASCIIConverterFactory = new ASCIIConverterFactory {
      override def apply(): ASCIIConverter = null
    }

    ASCIIConverters.register("test", testFactory)
    ASCIIConverters.getConverterFactory("test") shouldBe Some(testFactory)
  }

  it should "return registered converter factory" in {
    ASCIIConverters.getConverterFactory("default") shouldBe Some(DefaultASCIIConverterFactory)
    ASCIIConverters.getConverterFactory("extended") shouldBe Some(ExtendedASCIIConverterFactory)
    ASCIIConverters.getConverterFactory("nonlinear") shouldBe Some(NonlinearASCIIConverterFactory)
  }

  it should "return None for unknown converter" in {
    ASCIIConverters.getConverterFactory("unknown") shouldBe None
  }

  "DefaultASCIIConverter" should "convert grayscale values using default table" in {
    val image = List(
      List(0, 128, 255),
      List(64, 192, 128)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val converter = DefaultASCIIConverterFactory()

    val result = converter.convert(grayscaleImage)
    val lines = result.split(System.lineSeparator())

    lines(0) shouldEqual "@+ "
    lines(1) shouldEqual "#-+"
  }

  "ExtendedASCIIConverter" should "convert grayscale values using extended table" in {
    val image = List(
      List(0, 128, 255),
      List(64, 192, 128)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val converter = ExtendedASCIIConverterFactory()

    val result = converter.convert(grayscaleImage)
    val lines = result.split(System.lineSeparator())

    lines(0) shouldEqual "$n "
    lines(1) shouldEqual "q-n"
  }

  "NonlinearASCIIConverter" should "convert grayscale values using nonlinear mapping" in {
    val image = List(
      List(0, 128, 255),
      List(64, 192, 128)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val converter = NonlinearASCIIConverterFactory()

    val result = converter.convert(grayscaleImage)
    val lines = result.split(System.lineSeparator())

    lines.foreach { line =>
      line.length shouldBe 3
      line.forall(ch => "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ".contains(ch)) shouldBe true
    }
  }

  "LinearASCIIConverter" should "work with custom table" in {
    val image = List(
      List(0, 51, 102),
      List(153, 204, 255)
    )
    val grayscaleImage = GrayscaleImage(image).get
    val converter = new LinearASCIIConverter("#*Xx. ")

    val result = converter.convert(grayscaleImage)
    val lines = result.split(System.lineSeparator())

    lines(0) shouldEqual "#*X"
    lines(1) shouldEqual "x. "
  }

  "All converters" should "handle empty images" in {
    val emptyImage = GrayscaleImage(List(List())).get

    val defaultResult = DefaultASCIIConverterFactory().convert(emptyImage)
    val extendedResult = ExtendedASCIIConverterFactory().convert(emptyImage)
    val nonlinearResult = NonlinearASCIIConverterFactory().convert(emptyImage)

    defaultResult shouldBe ""
    extendedResult shouldBe ""
    nonlinearResult shouldBe ""
  }

  it should "handle single pixel images" in {
    val singlePixelImage = GrayscaleImage(List(List(128))).get

    val defaultResult = DefaultASCIIConverterFactory().convert(singlePixelImage)
    val extendedResult = ExtendedASCIIConverterFactory().convert(singlePixelImage)
    val nonlinearResult = NonlinearASCIIConverterFactory().convert(singlePixelImage)

    defaultResult.trim should have length 1
    extendedResult.trim should have length 1
    nonlinearResult.trim should have length 1
  }

  it should "handle edge cases correctly" in {
    val image = List(
      List(0, 255)
    )
    val grayscaleImage = GrayscaleImage(image).get

    val defaultResult = DefaultASCIIConverterFactory().convert(grayscaleImage)
    val extendedResult = ExtendedASCIIConverterFactory().convert(grayscaleImage)
    val nonlinearResult = NonlinearASCIIConverterFactory().convert(grayscaleImage)

    defaultResult shouldBe "@ "
    extendedResult shouldBe "$ "
    nonlinearResult shouldBe "$ "
  }
}