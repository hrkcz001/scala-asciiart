package ASCIIArt

import scala.util.{Failure, Success, Try}
import scala.util.boundary
import scala.util.boundary.break

case class GrayscaleImage private ( image: List[List[Int]]
                                  , scale: Float
                                  , rotation: Int
                                  , inverted: Boolean)

object GrayscaleImage {
  def apply(image: List[List[Int]]): Try[GrayscaleImage] = {
    val width = image.head.length
    boundary{
      for (row <- image) {
        if (row.length != width) {
          break(Failure(new IllegalArgumentException("All rows must have the same length")))
        }
        for (pixel <- row) {
          if (pixel < 0 || pixel > 255) {
            break(Failure(new IllegalArgumentException("All pixels must be between 0 and 255")))
          }
        }
      }
      Success(GrayscaleImage(image, 1.0f, 0, false))
    }
  }
  
    def invert(grayscaleImage: GrayscaleImage): GrayscaleImage =
      grayscaleImage.copy(inverted = !grayscaleImage.inverted)
    
    def rotate(degrees: Int)(grayscaleImage: GrayscaleImage): GrayscaleImage =
      grayscaleImage.copy(rotation = (((grayscaleImage.rotation + degrees / 90) % 4) + 4) % 4)
      
    def scale(factor: Float)(grayscaleImage: GrayscaleImage): GrayscaleImage =
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
        // don't do anything if we try to shrink the image to zero size
        if (squareFactor <= 0)
          return pixelsArray.toList //should be changed to boundary if new image alterations are added
        val squareSide = (1 / squareFactor).toInt
        if (squareSide == 0 || squareSide > pixelsArray.length || squareSide > pixelsArray(0).length)
          return pixelsArray.toList //should be changed to boundary if new image alterations are added
        
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
