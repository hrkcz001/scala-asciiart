import ASCIIArt.*
import ASCIIArt.ASCIIConverter.tableByName
import ASCIIArt.imageLoaders.{GeneratedImage, JpgImageLoader, PngImageLoader}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class InputParserSpec extends AnyFlatSpec with Matchers {

  "InputParser" should "parse a valid example arguments" in {
    //--image bird.jpg --output-file result.txt --output-console --scale 0.0625 --invert --table linear
    val tempFile = java.io.File.createTempFile("result", ".txt")
    val args = Array("--image", "bird.jpg", "--output-file", tempFile.getAbsolutePath, "--output-console", "--scale", "0.0625", "--invert", "--table", "linear")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage.get.isInstanceOf[JpgImageLoader] shouldBe true
    result.get.output should have size 2
    result.get.output should contain(Console.out)
    result.get.filters should have size 2
    result.get.filters should contain(Filter("--scale", List("0.0625")))
    result.get.filters should contain(Filter("--invert", List()))
    (0 to 255).map(result.get.table.get) shouldBe (0 to 255).map(tableByName("linear").get)

  }

  it should "parse a valid JPG image argument" in {
    val args = Array("--image", "test.jpg")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage.get.isInstanceOf[JpgImageLoader] shouldBe true
  }

  it should "parse a valid PNG image argument" in {
    val args = Array("--image", "test.png")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage.get.isInstanceOf[PngImageLoader] shouldBe true
  }

  it should "fail if multiple images are provided" in {
    val args = Array("--image", "test1.jpg", "--image", "test2.jpg")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Only one image can be provided")
  }

  it should "fail if local image and generated image are provided" in {
    val args = Array("--image", "test.jpg", "--image-generated")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Only one image can be provided")
  }

  it should "parse a generated image argument" in {
    val args = Array("--image-generated")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage.get.isInstanceOf[GeneratedImage] shouldBe true
  }

  it should "fail on unsupported image format" in {
    val args = Array("--image", "test.unsupported")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Unsupported image format")
  }

  it should "parse output to console" in {
    val args = Array("--output-console")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.output should contain(Console.out)
  }

  it should "fail if duplicate console output is specified" in {
    val args = Array("--output-console", "--output-console")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Only one console output can be requested")
  }

  it should "parse output to file" in {
    val tempFile = java.io.File.createTempFile("test", ".txt")
    tempFile.deleteOnExit()
    val args = Array("--output-file", tempFile.getAbsolutePath)
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.output should have size 1
  }

  it should "fail if no output file is specified" in {
    val args = Array("--output-file")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("No output file specified")
  }

  it should "parse a valid table argument" in {
    val args = Array("--table", "linear")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    (0 to 255).map(result.get.table.get) shouldBe (0 to 255).map(tableByName("linear").get)
  }

  it should "fail on unknown table name" in {
    val args = Array("--table", "unknown")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Table unknown not found")
  }

  it should "parse custom table" in {
    val args = Array("--custom-table", "@#")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    List(0, 255).map(result.get.table.get) shouldBe List('@', '#')
  }

  it should "fail if multiple tables are provided" in {
    val args = Array("--table", "linear", "--custom-table", "@#")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Only one table can be provided")
  }

  it should "parse filters with valid arguments" in {
    val args = Array("--scale", "0.25", "--rotate", "+90")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.filters should have size 2
    result.get.filters should contain(Filter("--scale", List("0.25")))
    result.get.filters should contain(Filter("--rotate", List("+90")))
  }

  it should "fail if required filter arguments are missing" in {
    val args = Array("--scale")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Expected 1 argument(s) for filter --scale")
  }

  it should "fail on unknown argument" in {
    val args = Array("--unknown")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Unknown argument")
  }
}