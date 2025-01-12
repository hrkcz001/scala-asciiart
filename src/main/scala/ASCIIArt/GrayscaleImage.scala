package ASCIIArt

import scala.util.{Failure, Success, Try}
import scala.util.boundary
import scala.util.boundary.break

case class GrayscaleImage private (pixels: List[List[Int]], width: Int, height: Int)

object GrayscaleImage {
  def apply(image: List[List[Int]]): Try[GrayscaleImage] = {
    val width = image.head.length
    boundary {
      for (row <- image) {
        if (row.length != width) {
          break(Failure(new Exception("All rows must have the same length")))
        }
        for (pixel <- row) {
          if (pixel < 0 || pixel > 255) {
            break(Failure(new Exception("All pixels must be between 0 and 255")))
          }
        }
      }
      Success(GrayscaleImage(image, width, image.length))
    }
  }
}
