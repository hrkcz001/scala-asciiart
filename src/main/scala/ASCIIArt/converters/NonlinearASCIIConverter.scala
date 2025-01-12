package ASCIIArt.converters

import ASCIIArt.GrayscaleImage

class NonlinearASCIIConverter extends ASCIIConverter {
  
  private val table = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
  
  private def toAscii (pixel: Int): Char = {
    var i = 1.0f
    while (i < table.length && pixel > (i / (i + 1) * 255)) i += 1
    table((i - 1).toInt)
  }

  override def convert(image: GrayscaleImage): String = {
    val asciiPixels = image.pixels.map(_.map(p => toAscii(p)))
    asciiPixels.map(_.mkString("")).mkString(System.lineSeparator())
  }
}

object NonlinearASCIIConverterFactory extends ASCIIConverterFactory {
  override def apply(): ASCIIConverter = new NonlinearASCIIConverter

  ASCIIConverters.register("nonlinear", this)
}
