package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

trait ProcessedImage {
  def getPixels: List[List[Pixel]]
}