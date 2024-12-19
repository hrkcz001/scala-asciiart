package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

import scala.util.Try

trait ProcessedImage {
  def getPixels: Try[List[List[Pixel]]]
}