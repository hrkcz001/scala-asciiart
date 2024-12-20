package ASCIIArt

val ASCII_TABLE: String =
  "@%#*+=-:. "
val EXTENDED_ASCII_TABLE: String =
  "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "

object ASCIIConverter {
  def tableByName(tableName: String): Option[Int => Char] = tableName match {
    case "linear" => Some(linearTable(ASCII_TABLE))
    case "extended" => Some(linearTable(EXTENDED_ASCII_TABLE))
    case "nonlinear" => Some((value: Int) =>
      var i = 1.0f
      while (i < EXTENDED_ASCII_TABLE.length && value > (i / (i + 1) * 255) ) i += 1
      EXTENDED_ASCII_TABLE((i - 1).toInt)
    )
    case _ => None
  }

  def linearTable(table: String): Int => Char =
    (value: Int) => table((value * (table.length - 1)) / 255)

  def convertToASCII(grayscaleImage: List[List[Int]], table: Int => Char): List[String] =
    grayscaleImage.map(row => row.map(table).mkString)
}