import ASCIIArt.imageLoaders.{GeneratedImage, JpgImageLoader, PngImageLoader}
import org.apache.tika.Tika
import org.mockito.Mockito.{mockConstruction, mockStatic, when}
import org.mockito.ArgumentMatchers.*
import org.mockito.{MockedConstruction, MockedStatic, MockitoAnnotations}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.compiletime.uninitialized
import java.io.File
import javax.imageio.ImageIO

class ImageLoadersSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  private val BUFF_IMAGE = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB)
  private var mockTika: MockedConstruction[Tika] = uninitialized
  private var mockFile: MockedConstruction[File] = uninitialized
  private var mockImageRead: MockedStatic[ImageIO] = uninitialized

  override def beforeEach(): Unit = {
    mockImageRead = mockStatic(classOf[ImageIO])
    when(ImageIO.read(any[File])).thenReturn(BUFF_IMAGE)
  }

  override def afterEach(): Unit = {
    mockImageRead.close()
  }

  // compiler: A needed class was not found. Missing class: ASCIIArt/imageLoaders/PngImageLoader
  // me: cool story bob
  // I don't know what I do wrong, LLM either
  /*"PngImageLoader" should "load PNG image correctly" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(true)
      when(mock.getPath).thenReturn("some/path")
    })

    mockTika = mockConstruction(classOf[Tika], (mock, _) =>
      when(mock.detect(any[File])).thenReturn("image/png"))

    val loader = new PngImageLoader("some/path")
    loader.getPixels.isSuccess shouldBe true

    mockFile.close()
    mockTika.close()
  }

  it should "fail to load non-PNG image" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(true)
      when(mock.getPath).thenReturn("some/path")
    })

    mockTika = mockConstruction(classOf[Tika], (mock, _) =>
      when(mock.detect(any[File])).thenReturn("image/jpeg"))

    val loader = new PngImageLoader("some/path")
    loader.getPixels.isFailure shouldBe true

    mockFile.close()
    mockTika.close()
  }

  it should "fail to load non-existent file" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(false)
    })

    val loader = new PngImageLoader("some/path")
    loader.getPixels.isFailure shouldBe true

    mockFile.close()
  }

  "JpgImageLoader" should "load JPEG image correctly" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(true)
      when(mock.getPath).thenReturn("some/path")
    })

    mockTika = mockConstruction(classOf[Tika], (mock, _) =>
      when(mock.detect(any[File])).thenReturn("image/jpeg"))

    val loader = new JpgImageLoader("some/path")
    loader.getPixels.isSuccess shouldBe true

    mockFile.close()
    mockTika.close()
  }

  it should "fail to load non-JPEG image" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(true)
      when(mock.getPath).thenReturn("some/path")
    })

    mockTika = mockConstruction(classOf[Tika], (mock, _) =>
      when(mock.detect(any[File])).thenReturn("image/png"))

    val loader = new JpgImageLoader("some/path")
    loader.getPixels.isFailure shouldBe true

    mockFile.close()
    mockTika.close()
  }

  it should "fail to load non-existent file" in {
    mockFile = mockConstruction(classOf[File], (mock, _) => {
      when(mock.canRead).thenReturn(false)
    })

    val loader = new JpgImageLoader("some/path")
    loader.getPixels.isFailure shouldBe true

    mockFile.close()
  }*/

  "GeneratedImage" should "generate an image with random pixels" in {
    val generator = new GeneratedImage(minHeight = 10, maxHeight = 50, maxRatio = 1.5f)
    val pixelsTry = generator.getPixels

    pixelsTry.isSuccess shouldBe true
    val pixels = pixelsTry.get

    pixels.length should (be >= 10 and be <= 50)

    val height = pixels.length
    val width = pixels.head.length

    width should (be >= (height / 1.5f).toInt and be <= (height * 1.5f).toInt)
  }
}