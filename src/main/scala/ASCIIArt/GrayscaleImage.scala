package ASCIIArt

import scala.util.{Failure, Success, Try}
import scala.util.control.Breaks._

case class GrayscaleImage private ( image: List[List[Int]]
                                  , scale: Float
                                  , rotation: Int
                                  , inverted: Boolean)

object GrayscaleImage {
  def apply(image: List[List[Int]]): Try[GrayscaleImage] = {
    val width = image.head.length
    var result: Try[GrayscaleImage] = Success(GrayscaleImage(image, 1.0f, 0, false))
    for (row <- image) {
      if (result.isFailure) {
        break
      }
      if (row.length != width) {
        result = Failure(new IllegalArgumentException("All rows must have the same length"))
      }
      for (pixel <- row) {
        if (pixel < 0 || pixel > 255) {
          result = Failure(new IllegalArgumentException("All pixels must be between 0 and 255"))
        }
      }
    }
    result
  }
  
    def invert(grayscaleImage: GrayscaleImage): GrayscaleImage =
      grayscaleImage.copy(inverted = !grayscaleImage.inverted)
    
    def rotate(grayscaleImage: GrayscaleImage, degrees: Int): GrayscaleImage =
      grayscaleImage.copy(rotation = (((grayscaleImage.rotation + degrees / 90) % 4) + 4) % 4)
      
    def scale(grayscaleImage: GrayscaleImage, factor: Float): GrayscaleImage =
        grayscaleImage.copy(scale = grayscaleImage.scale * factor)
        
    def process(grayscaleImage: GrayscaleImage): List[List[Int]] = {
      var result = grayscaleImage.image
      
      // Inversion
      if (grayscaleImage.inverted) {
        result = result.map(_.map(pixel => 255 - pixel))
      }
      
      // Rotation
      for (_ <- 0 until grayscaleImage.rotation) {
        result = result.transpose.map(_.reverse)
      }
      
      // Scaling
      val pixelsArray = result.toArray
      result = List[List[Int]]()
      val squareFactor = Math.sqrt(grayscaleImage.scale)
      if (squareFactor > 1) {
        val squareSide = squareFactor.toInt
        for (i <- pixelsArray.indices) {
          var row = List[Int]()
          for (j <- pixelsArray(i).indices) {
            for (_ <- 0 until squareSide) {
              row = row.appended(pixelsArray(i)(j))
            }
          }
          for (_ <- 0 until squareSide) {
            result = result.appended(row)
          }
        }
      }
      else {
        if (squareFactor <= 0)
          return result
        val squareSide = (1 / squareFactor).toInt
        if (squareSide == 0 || squareSide > pixelsArray.length || squareSide > pixelsArray(0).length)
          return result
        for (i <- pixelsArray.indices by squareSide) {
          var row = List[Int]()
          for (j <- pixelsArray(i).indices by squareSide) {
            var sum = 0
            for (k <- i until i + squareSide) {
              for (l <- j until j + squareSide) {
                sum += pixelsArray(k)(l)
              }
            }
            val average = sum / (squareSide * squareSide)
            row = row.appended(average)
          }
          result = result.appended(row)
        }
      }
      
      result
    }
}
