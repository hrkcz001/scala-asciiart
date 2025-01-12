package ASCIIArt.filters

import ASCIIArt.GrayscaleImage

import scala.util.{Failure, Success, Try}

class InvertFilter extends Filter {
  override def applyFilter(image: GrayscaleImage): Try[GrayscaleImage] = {
    val invertedPixels = image.pixels.map(_.map(p => 255 - p))
    GrayscaleImage(invertedPixels)
  }
}

object InvertFilterFactory extends FilterFactory {
  override def apply(arguments: List[String]): Try[Filter] =
    if (arguments.nonEmpty)
      Failure(new IllegalArgumentException("No arguments are required for filter --invert"))
    else
      Success(new InvertFilter)
  
  Filters.register("invert", this)
}