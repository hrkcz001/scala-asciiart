package ASCIIArt.filters

import ASCIIArt.GrayscaleImage

import scala.util.{Failure, Success, Try}

trait Filter {
  def applyFilter(image: GrayscaleImage): Try[GrayscaleImage]
}

trait FilterFactory {
  def apply(arguments: List[String]): Try[Filter]
}

object Filters {
  private var filters: Map[String, FilterFactory] = Map()
    
  def register(name: String, filter: FilterFactory): Unit = {
    filters += (name -> filter)
  }
    
  def getFilterFactory(name: String): Option[FilterFactory] = {
    filters.get(name)
  }
}