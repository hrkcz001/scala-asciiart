package ASCIIArt.converters

import ASCIIArt.GrayscaleImage

class LinearASCIIConverter(table: String) extends ASCIIConverter {
  
  private def toAscii(pixel: Int): Char = {
    val asciiIndex = (pixel * (table.length - 1)) / 255
    table(asciiIndex)
  }
  
  override def convert(image: GrayscaleImage): String = {
    val asciiPixels = image.pixels.map(_.map(p => toAscii(p)))
    asciiPixels.map(_.mkString("")).mkString(System.lineSeparator())
  }
}

object DefaultASCIIConverterFactory extends ASCIIConverterFactory {
  private val defaultTable: String = "@%#*+=-:. "
  override def apply(): ASCIIConverter = new LinearASCIIConverter(defaultTable)
  ASCIIConverters.register("default", this)
}

object ExtendedASCIIConverterFactory extends ASCIIConverterFactory {
  private val extendedTable: String = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
  override def apply(): ASCIIConverter = new LinearASCIIConverter(extendedTable)
  ASCIIConverters.register("extended", this)
}