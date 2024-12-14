package ASCIIArt

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
}