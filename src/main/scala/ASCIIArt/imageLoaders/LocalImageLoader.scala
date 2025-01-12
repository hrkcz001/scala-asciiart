package ASCIIArt.imageLoaders

import ASCIIArt.Pixel
import org.apache.tika.Tika

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

trait PixelReader {
  def read(file: File): List[List[Pixel]]
}

object DefaultPixelReader extends PixelReader {
  def bufferedImageToPixels(image: BufferedImage): List[List[Pixel]] = {
    for {
      y <- 0 until image.getHeight
    } yield for {
      x <- 0 until image.getWidth
    } yield {
      //transparent means white
      val rgb = image.getRGB(x, y)
      if ((rgb >> 24) == 0)
        Pixel(255, 255, 255).get
      else
        Pixel((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF).get
    }
  }.toList.map(_.toList)
  
  override def read(file: File): List[List[Pixel]] = bufferedImageToPixels(ImageIO.read(file))
}

trait MimeDetector {
  def detect(file: File): String
}

object TikaMimeDetector extends MimeDetector {
  private val tika = new Tika()

  def detect(file: File): String = tika.detect(file)
}

abstract class LocalImageLoader(file: File, mimeDetector: MimeDetector) extends ImageLoader {
  protected val mime: String = try { mimeDetector.detect(file) } catch { case _: Throwable => "" }
  protected val wasFound: Try[Boolean] = if (!file.canRead) {
    Failure(new IllegalArgumentException(s"File not found"))
  } else {
    Success(true)
  }
}