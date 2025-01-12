package ASCIIArt

import scala.util.{Failure, Success, Try}

case class ParsedArgs( processedImage: Option[Option[String]] = None
                     , output: List[String] = List()
                     , console: Boolean = false
                     , filters: List[(String, List[String])] = List()
                     , table: Option[Either[String, String]] = None
                     )

// Parses input arguments semantically
object InputParser {
  def parse(args: Array[String]): Try[ParsedArgs] = {
    var parsed: Try[ParsedArgs] = Success(ParsedArgs())
    var i = 0
    while (args != null && i < args.length && parsed.isSuccess) {
      args(i) match {
        case "--image" =>
          if (parsed.get.processedImage.isDefined)
            parsed = Failure(new IllegalArgumentException("Only one image can be provided"))
          else
          if (i + 1 >= args.length || args(i + 1).startsWith("--"))
            parsed = Failure(new IllegalArgumentException("No image file specified"))
          else
            i += 1
            parsed = parsed.map(s => s.copy(processedImage = Some(Some(args(i)))))
        case "--image-generated" =>
          if (parsed.get.processedImage.isDefined)
            parsed = Failure(new IllegalArgumentException("Only one image can be provided"))
          else
            parsed = parsed.map(s => s.copy(processedImage = Some(None)))
        case "--output-console" =>
          if (!parsed.get.console)
            parsed = parsed.map(s => s.copy(console = true))
          else
            parsed = Failure(new IllegalArgumentException("Only one console output can be requested"))
        case "--output-file" =>
          if (i + 1 >= args.length || args(i + 1).startsWith("--"))
            parsed = Failure(new IllegalArgumentException("No output file specified"))
          else
            i += 1
            if (parsed.get.output.contains(args(i)))
              parsed = Failure(new IllegalArgumentException("Same output file specified multiple times"))
            else
              parsed = parsed.map(s => s.copy(output = s.output :+ args(i)))
        case "--table" =>
            if (parsed.get.table.isDefined)
                parsed = Failure(new IllegalArgumentException("Only one table can be provided"))
            else
            if (i + 1 >= args.length || args(i + 1).startsWith("--"))
              parsed = Failure(new IllegalArgumentException("No table name specified"))
            else
              i += 1
              parsed = parsed.map(s => s.copy(table = Some(Left(args(i)))))
        case "--custom-table" =>
            if (parsed.get.table.isDefined)
                parsed = Failure(new IllegalArgumentException("Only one table can be provided"))
            else
            if (i + 1 >= args.length)
              parsed = Failure(new IllegalArgumentException("No custom table specified"))
            else
              i += 1
              if (args(i).equals(""))
                parsed = Failure(new IllegalArgumentException("Custom table cannot be empty"))
              else
                parsed = parsed.map(s => s.copy(table = Some(Right(args(i)))))
        case _ =>
            if (args(i).startsWith("--"))
                val filter = args(i).drop(2)
                var arguments: List[String] = List()
                while (i + 1 < args.length && !args(i + 1).startsWith("--")) {
                    i += 1
                    arguments :+= args(i)
                }
                parsed = parsed.map(s => s.copy(filters = s.filters :+ (filter, arguments)))
            else
                parsed = Failure(new IllegalArgumentException(s"Unknown argument ${args(i)}"))
      }
      i += 1
    }
    parsed
  }
}

