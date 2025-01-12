package ASCIIArt.filters

import ASCIIArt.GrayscaleImage

import scala.util.{Failure, Success, Try}

class ScaleFilter(scale: Float) extends Filter {
  override def applyFilter(image: GrayscaleImage): Try[GrayscaleImage] = {
    var result = List[List[Int]]()
    val pixelsArray = image.pixels.toArray
    val squareFactor = Math.sqrt(scale)
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
        return Success(image)
      val squareSide = (1 / squareFactor).toInt
      if (squareSide == 0 || squareSide > pixelsArray.length || squareSide > pixelsArray(0).length)
        return Success(image)

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
    GrayscaleImage(result)
  }
}

object ScaleFilterFactory extends FilterFactory {
  override def apply(arguments: List[String]): Try[Filter] = {
    if (arguments.length != 1)
      Failure(new IllegalArgumentException("Exactly one argument is required for filter --scale"))
    else
      Try(arguments.head.toFloat) match {
        case Success(scale) => Success(new ScaleFilter(scale))
        case Failure(_) => Failure(new IllegalArgumentException("Argument for filter --scale must be a float"))
      }
  }
  
  Filters.register("scale", this)
}
