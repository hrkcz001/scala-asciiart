package ASCIIArt

import ASCIIArt.GrayscaleImage.{invert, rotate, scale}

import scala.util.{Failure, Success, Try}

case class Filter(name: String, arguments: List[String])

object Filter {

  // filter name, (number of arguments, their description), function to apply the filter
  private val filters: List[(String, (Int, String), List[String] => (GrayscaleImage => GrayscaleImage))] = List(
    ("--invert", (0, "None"), _ => invert),
    ("--rotate", (1, "Int"), (args) => rotate(args.head.trim.toInt)),
    ("--scale", (1, "Float"), (args) => scale(args.head.trim.toFloat))
  )

  //also serves as a way to check if a filter is valid  
  def argumentsRequired(filter: String): Option[Int] = {
    filters.find(_._1 == filter).map(_._2._1)
  }

  def applyFilter(grayscaleImage: GrayscaleImage, filter: Filter): Try[GrayscaleImage] = {
    filters.find(_._1 == filter.name) match {
      case None => Failure(new IllegalArgumentException(s"Unknown filter ${filter.name}"))
      case Some(filterDef) =>
        if (filterDef._2._1 != filter.arguments.length) {
          Failure(new IllegalArgumentException(s"Expected ${argumentsRequired(filter.name).get} arguments for filter ${filter.name}"))
        }
        else {
          Try(filterDef._3(filter.arguments)) match {
            case Success(f) => Success(f(grayscaleImage))
            case Failure(_) => Failure(new IllegalArgumentException(s"${filterDef._2._2} argument(s) required for filter ${filter.name}"))
          }
        }
    }
  }
}