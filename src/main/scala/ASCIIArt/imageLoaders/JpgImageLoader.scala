package ASCIIArt.imageLoaders

import ASCIIArt.Pixel
import ASCIIArt.util.bufferedImageToPixels

import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

class JpgImageLoader(path: String) extends LocalImageLoader(path) {
  
  private val isJpeg = if (mime != "image/jpeg") {
    Failure(new IllegalArgumentException(s"File $path is not a JPEG file"))
  } else {
    Success(true)
  }

  private val image = if (wasFound.isSuccess) ImageIO.read(file) else null
  private val pixels = if (image != null) bufferedImageToPixels(image) else List[List[Pixel]]()

  override def getPixels: Try[List[List[Pixel]]] = wasFound.flatMap(_ => isJpeg.map(_ => pixels))
}
