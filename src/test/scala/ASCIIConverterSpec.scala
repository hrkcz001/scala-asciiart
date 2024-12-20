import ASCIIArt.ASCIIConverter.{convertToASCII, linearTable, tableByName}
import ASCIIArt.{ASCII_TABLE, EXTENDED_ASCII_TABLE}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ASCIIConverterSpec extends AnyFlatSpec with Matchers {

  "ASCIIConverter" should "return a linear ASCII table for 'linear'" in {
    val tableOpt = tableByName("linear")
    tableOpt shouldBe defined

    val fun = tableOpt.get
    fun(0) shouldBe ASCII_TABLE.charAt(0)
    fun(128) shouldBe ASCII_TABLE.charAt(ASCII_TABLE.length / 2 - 1)
    fun(255) shouldBe ASCII_TABLE.charAt(ASCII_TABLE.length - 1)
  }

  it should "return an extended ASCII table for 'extended'" in {
    val tableOpt = tableByName("extended")
    tableOpt shouldBe defined

    val fun = tableOpt.get
    fun(0) shouldBe EXTENDED_ASCII_TABLE.charAt(0)
    fun(128) shouldBe EXTENDED_ASCII_TABLE.charAt(EXTENDED_ASCII_TABLE.length / 2 - 1)
    fun(255) shouldBe EXTENDED_ASCII_TABLE.charAt(EXTENDED_ASCII_TABLE.length - 1)
  }

  it should "return None for an unknown table name" in {
    val tableOpt = tableByName("unknown")
    tableOpt shouldBe None
  }

  it should "convert a grayscale image to ASCII using a linear table" in {
    val grayscaleImage = List(List(0, 128, 255), List(64, 192, 128))
    val fun = tableByName("linear").get
    val asciiImage = convertToASCII(grayscaleImage, fun)

    asciiImage shouldEqual List("@+ ", "#-+")
  }

  it should "convert a grayscale image to ASCII using an extended table" in {
    val grayscaleImage = List(List(0, 128, 255), List(64, 192, 128))
    val fun = tableByName("extended").get
    val asciiImage = convertToASCII(grayscaleImage, fun)

    asciiImage shouldEqual List("$n ", "q-n")
  }

  it should "convert a grayscale image to ASCII using a custom linear table" in {
    val grayscaleImage = List(List(0, 51, 102), List(153, 204, 255))
    val table = "#*Xx. "
    val fun = linearTable(table)
    val asciiImage = convertToASCII(grayscaleImage, fun)

    asciiImage shouldEqual List("#*X", "x. ")
  }

  it should "convert a grayscale image to ASCII using a custom table function" in {
    val grayscaleImage = List( List( 0,  1), List( 2,  3), List( 4,  5), List( 6,  7), List( 8,  9)
                             , List(10, 11), List(12, 13), List(14, 15), List(16, 17), List(18, 19))
    val fun = (value: Int) => (value % 10).toString.charAt(0)
    val asciiImage = convertToASCII(grayscaleImage, fun)

    asciiImage shouldEqual List("01", "23", "45", "67", "89", "01", "23", "45", "67", "89")
  }
}