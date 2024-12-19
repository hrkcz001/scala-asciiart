package ASCIIArt

val ASCII_TABLE: String =
  "@%#*+=-:. "
val EXTENDED_ASCII_TABLE: String =
  "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "

object ASCIIConverter {
  def tableByName(tableName: String): Option[String] = tableName match {
    case "linear" => Some(ASCII_TABLE)
    case "nonlinear" => Some(EXTENDED_ASCII_TABLE)
    case _ => None
  }

  def linearTable(table: String): Int => Char =
    // not linear when 255 is not divisible by table length
    (value: Int) => table((value * (table.length - 1)) / 255)

  def convertToASCII(grayscaleImage: List[List[Int]], table: Int => Char): List[String] =
    grayscaleImage.map(row => row.map(table).mkString)
}