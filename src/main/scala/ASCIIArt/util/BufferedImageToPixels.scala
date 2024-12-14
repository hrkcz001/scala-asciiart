package ASCIIArt.util

import ASCIIArt.Pixel

import java.awt.image.BufferedImage

def bufferedImageToPixels(image: BufferedImage): List[List[Pixel]] = {
  for {
    y <- 0 until image.getHeight
  } yield for {
    x <- 0 until image.getWidth
  } yield {
    val rgb = image.getRGB(x, y)
    Pixel((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF).get
  }
}.toList.map(_.toList)
