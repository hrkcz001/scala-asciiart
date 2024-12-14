package ASCIIArt.imageLoaders

import ASCIIArt.Pixel
import ASCIIArt.util.bufferedImageToPixels

import javax.imageio.ImageIO

class JpgImageLoader(path: String) extends LocalImageLoader(path) {
  
  if (mime != "image/jpeg") {
    throw new Exception("File is not a JPG image")
  }

  private val image = ImageIO.read(file)
  private val pixels = bufferedImageToPixels(image)

  override def getPixels: List[List[Pixel]] = pixels.map(_.toList)
}
