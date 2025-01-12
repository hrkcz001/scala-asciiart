package ASCIIArt.converters

import ASCIIArt.GrayscaleImage

trait ASCIIConverter {
  def convert(image: GrayscaleImage): String
}

trait ASCIIConverterFactory {
  def apply(): ASCIIConverter
}

object ASCIIConverters {
  private var converters: Map[String, ASCIIConverterFactory] = Map()

  def register(name: String, converter: ASCIIConverterFactory): Unit = {
    converters += (name -> converter)
  }

  def getConverterFactory(name: String): Option[ASCIIConverterFactory] = {
    converters.get(name)
  }
}