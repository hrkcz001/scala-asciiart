package ASCIIArt

object InputParser {
  def parse(args: Array[String]): Option[(String, String, String, String, String)] = {
    if (args.length < 5) {
      None
    } else {
      val input = args(0)
      val output = args(1)
      val filter = args(2)
      val filterArgs = args.slice(3, args.length - 1)
      val filterArg = args(args.length - 1)
      Some((input, output, filter, filterArgs.mkString(" "), filterArg))
    }
  }
}

