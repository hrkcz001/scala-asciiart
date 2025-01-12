package ASCIIArt.testing

import ASCIIArt.InputParser
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
    result.get.processedImage shouldBe Some(Some("bird.jpg"))
    result.get.output shouldBe List(tempFile.getAbsolutePath)
    result.get.console shouldBe true
    result.get.filters should have size 2
    result.get.filters should contain("scale", List("0.0625"))
    result.get.filters should contain("invert", List())
    result.get.table shouldBe Some(Left("linear"))

  }

  it should "parse a valid image argument" in {
    val args = Array("--image", "test.jpg")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage shouldBe Some(Some("test.jpg"))
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

  it should "fail if no image file is specified" in {
    val args = Array("--image")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("No image file specified")
  }

  it should "parse a generated image argument" in {
    val args = Array("--image-generated")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.processedImage shouldBe Some(None)
  }

  it should "parse output to console" in {
    val args = Array("--output-console")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.console shouldBe true
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
    result.get.output shouldBe List(tempFile.getAbsolutePath)
  }

  it should "fail if no output file is specified" in {
    val args = Array("--output-file", "--output-file", "result.txt")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("No output file specified")
  }

  it should "fail if same output file is specified multiple times" in {
    val tempFile = java.io.File.createTempFile("result", ".txt")
    tempFile.deleteOnExit()
    val args = Array("--output-file", tempFile.getAbsolutePath, "--output-file", tempFile.getAbsolutePath)
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Same output file specified multiple times")
  }

  it should "parse a valid table argument" in {
    val args = Array("--table", "linear")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.table shouldBe Some(Left("linear"))
  }

  it should "parse custom table" in {
    val args = Array("--custom-table", "@#")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.table shouldBe Some(Right("@#"))
  }

  it should "fail if multiple tables are provided" in {
    val args = Array("--table", "linear", "--custom-table", "@#")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Only one table can be provided")
  }

  it should "fail if no table name is specified" in {
    val args = Array("--table")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("No table name specified")
  }

  it should "fail if no custom table is specified" in {
    val args = Array("--custom-table")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("No custom table specified")
  }

  it should "fail if custom table is empty" in {
    val args = Array("--custom-table", "")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Custom table cannot be empty")
  }

  it should "parse filters with valid arguments" in {
    val args = Array("--scale", "0.25", "--rotate", "+90")
    val result = InputParser.parse(args)

    result shouldBe a[Success[?]]
    result.get.filters should have size 2
    result.get.filters should contain("scale", List("0.25"))
    result.get.filters should contain("rotate", List("+90"))
  }

  it should "fail on unknown argument" in {
    val args = Array("unknown")
    val result = InputParser.parse(args)

    result shouldBe a[Failure[?]]
    result.failed.get.getMessage should include("Unknown argument")
  }
}