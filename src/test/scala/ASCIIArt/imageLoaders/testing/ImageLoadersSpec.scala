package ASCIIArt.imageLoaders.testing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import ASCIIArt.imageLoaders._
import ASCIIArt.Pixel

import java.io.File
import scala.util.Success
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

class ImageLoadersSpec extends AnyFlatSpec with Matchers with MockitoSugar {
  PngImageLoaderFactory
  JpgImageLoaderFactory

  private val testPixels = List(
    List(Pixel(255, 0, 0).get, Pixel(0, 255, 0).get),
    List(Pixel(0, 0, 255).get, Pixel(255, 255, 255).get)
  )

  "ImageLoaders registry" should "handle loader registration and retrieval" in {
    val testFactory = new ImageLoaderFactory {
      override def apply(path: String): ImageLoader = null
    }

    ImageLoaders.register("test", testFactory)
    ImageLoaders.getLoaderFactory("test") shouldBe Some(testFactory)
  }

  it should "return registered loader factory" in {
    ImageLoaders.getLoaderFactory("png") shouldBe Some(PngImageLoaderFactory)
    ImageLoaders.getLoaderFactory("jpg") shouldBe Some(JpgImageLoaderFactory)
  }

  it should "return None for unknown loader" in {
    ImageLoaders.getLoaderFactory("unknown") shouldBe None
  }

  "PngImageLoader" should "successfully load PNG image" in {
    var tempFile: File = null
    val mockMimeDetector = mock[MimeDetector]
    val mockPixelReader = mock[PixelReader]

    when(mockMimeDetector.detect(any[File])).thenReturn("image/png")
    when(mockPixelReader.read(any[File])).thenReturn(testPixels)

    val originalMimeDetector = PngImageLoaderFactory.getMimeDetector
    val originalPixelReader = PngImageLoaderFactory.getPixelReader

    try {
      tempFile = File.createTempFile("test", ".png")
      PngImageLoaderFactory.set(mockMimeDetector)
      PngImageLoaderFactory.set(mockPixelReader)

      val loader = PngImageLoaderFactory(tempFile.getAbsolutePath)
      val result = loader.getPixels

      result shouldBe Success(testPixels)
    } finally {
      tempFile.delete()
      PngImageLoaderFactory.set(originalMimeDetector)
      PngImageLoaderFactory.set(originalPixelReader)
    }
  }

  it should "fail for non-PNG files" in {
    var tempFile: File = null
    val mockMimeDetector = mock[MimeDetector]
    when(mockMimeDetector.detect(any[File])).thenReturn("image/jpeg")

    val originalMimeDetector = PngImageLoaderFactory.getMimeDetector

    try {
      tempFile = File.createTempFile("test", ".jpg")
      PngImageLoaderFactory.set(mockMimeDetector)

      val loader = PngImageLoaderFactory(tempFile.getAbsolutePath)
      val result = loader.getPixels

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("not a PNG file")
    } finally {
      tempFile.delete()
      PngImageLoaderFactory.set(originalMimeDetector)
    }
  }

  it should "fail for nonexistent files" in {
    val loader = PngImageLoaderFactory("nonexistent.png")
    val result = loader.getPixels

    result.isFailure shouldBe true
    result.failed.get.getMessage should include("not found")
  }

  "JpgImageLoader" should "successfully load JPEG image" in {
    var tempFile: File = null
    val mockMimeDetector = mock[MimeDetector]
    val mockPixelReader = mock[PixelReader]

    when(mockMimeDetector.detect(any[File])).thenReturn("image/jpeg")
    when(mockPixelReader.read(any[File])).thenReturn(testPixels)

    val originalMimeDetector = JpgImageLoaderFactory.getMimeDetector
    val originalPixelReader = JpgImageLoaderFactory.getPixelReader

    try {
      tempFile = File.createTempFile("test", ".jpg")
      JpgImageLoaderFactory.set(mockMimeDetector)
      JpgImageLoaderFactory.set(mockPixelReader)

      val loader = JpgImageLoaderFactory(tempFile.getAbsolutePath)
      val result = loader.getPixels

      result shouldBe Success(testPixels)
    } finally {
      tempFile.delete()
      JpgImageLoaderFactory.set(originalMimeDetector)
      JpgImageLoaderFactory.set(originalPixelReader)
    }
  }

  it should "fail for non-JPEG files" in {
    var tempFile: File = null
    val mockMimeDetector = mock[MimeDetector]
    when(mockMimeDetector.detect(any[File])).thenReturn("image/png")

    val originalMimeDetector = JpgImageLoaderFactory.getMimeDetector

    try {
      tempFile = File.createTempFile("test", ".png")
      JpgImageLoaderFactory.set(mockMimeDetector)

      val loader = JpgImageLoaderFactory(tempFile.getAbsolutePath)
      val result = loader.getPixels

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("not a JPEG file")
    } finally {
      tempFile.delete()
      JpgImageLoaderFactory.set(originalMimeDetector)
    }
  }

  it should "fail for nonexistent files" in {
    val loader = JpgImageLoaderFactory("nonexistent.jpg")
    val result = loader.getPixels

    result.isFailure shouldBe true
    result.failed.get.getMessage should include("not found")
  }

  "GeneratedImage" should "generate valid image with default parameters" in {
    val generator = new GeneratedImage()
    val result = generator.getPixels

    result.isSuccess shouldBe true
    val pixels = result.get

    pixels.length should (be >= MIN_HEIGHT and be <= MAX_HEIGHT)

    val ratio = pixels.head.length.toFloat / pixels.length
    ratio should (be >= (1.0f / MAX_RATIO) and be <= MAX_RATIO)

    pixels.flatten.foreach { pixel =>
      pixel.red should (be >= 0 and be <= 255)
      pixel.green should (be >= 0 and be <= 255)
      pixel.blue should (be >= 0 and be <= 255)
    }
  }

  it should "generate valid image with custom parameters" in {
    val minHeight = 5
    val maxHeight = 15
    val maxRatio = 1.5f

    val generator = new GeneratedImage(minHeight, maxHeight, maxRatio)
    val result = generator.getPixels

    result.isSuccess shouldBe true
    val pixels = result.get

    pixels.length should (be >= minHeight and be <= maxHeight)

    val ratio = pixels.head.length.toFloat / pixels.length
    ratio should (be >= (1.0f / maxRatio) and be <= maxRatio)
  }
}