package ASCIIArt

import scala.math.round

case class Pixel private (red: Int, green: Int, blue: Int)

object Pixel {
  def apply(red: Int, green: Int, blue: Int): Option[Pixel] = {
    if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
      None
    } else {
      Some(new Pixel(red, green, blue))
    }
  }

  def grayscale(pixel: Pixel): Int = round((0.3f * pixel.red) + (0.59f * pixel.green) + (0.11f * pixel.blue))
}
