package ASCIIArt

import ASCIIArt.GrayscaleImage.{invert, rotate, scale}

import scala.util.{Failure, Success, Try}

case class Filter(name: String, arguments: List[String])

object Filter {
  //also serves as a way to check if a filter is valid  
  def argumentsRequired(filter: String): Option[Int] = {
    filter match {
      case "--invert" => Some(0)
      case "--rotate" => Some(1)
      case "--scale" => Some(1)
      case _ => None
    }
  }
  
  def applyFilter(grayscaleImage: GrayscaleImage, filter: Filter): Try[GrayscaleImage] = {
    filter.name match {
      case "--invert" => Success(invert(grayscaleImage))
      case "--rotate" => try{ Success(rotate(grayscaleImage, filter.arguments.head.trim.toInt)) } 
                         catch { case _: NumberFormatException => Failure(new IllegalArgumentException("Rotation must be an integer")) }
      case "--scale" => try{ Success(scale(grayscaleImage, filter.arguments.head.trim.toFloat)) }
                         catch { case _: NumberFormatException => Failure(new IllegalArgumentException("Scale must be a float")) }
    }
  }
}