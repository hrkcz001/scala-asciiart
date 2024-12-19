package ASCIIArt.imageLoaders

import ASCIIArt.Pixel
import ASCIIArt.util.bufferedImageToPixels

import javax.imageio.ImageIO
import scala.util.{Try, Failure, Success}

class PngImageLoader(path: String) extends LocalImageLoader(path) {
  
  private val isPng = if (mime != "image/png") {
    Failure(new IllegalArgumentException(s"File $path is not a PNG file"))
  } else {
    Success(true)
  }
  
  private val image = if (isPng.isSuccess) ImageIO.read(file) else null
  private val pixels = if (image != null) bufferedImageToPixels(image) else List[List[Pixel]]()

  override def getPixels: Try[List[List[Pixel]]] = wasFound.flatMap(_ => isPng.map(_ => pixels))
}