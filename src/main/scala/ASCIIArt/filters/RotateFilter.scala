package ASCIIArt.filters

import ASCIIArt.GrayscaleImage

import scala.util.{Failure, Success, Try}

class RotateFilter(angle: Int) extends Filter {
  override def applyFilter(image: GrayscaleImage): Try[GrayscaleImage] = {
    val rotations = ((angle % 360 + 360) % 360) / 90
    val rotatedPixels = (0 until rotations).foldLeft(image.pixels) { (pixels, _) =>
      pixels.transpose.map(_.reverse)
    }
    GrayscaleImage(rotatedPixels)
  }
}

object RotateFilterFactory extends FilterFactory {
  override def apply(arguments: List[String]): Try[Filter] = {
    if (arguments.length != 1) {
      return util.Failure(new IllegalArgumentException("Exactly one argument is required for filter --rotate"))
    }
    Try(arguments.head.toInt) match {
      case Success(angle) => Success(new RotateFilter(angle))
      case Failure(exception) => Failure(new IllegalArgumentException("Argument for filter --rotate must be an integer"))
    }
  }

  Filters.register("rotate", this)
}