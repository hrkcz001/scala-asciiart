package ASCIIArt

import ASCIIArt.ASCIIConverter.tableByName
import ASCIIArt.imageLoaders.{GeneratedImage, JpgImageLoader, PngImageLoader}

import java.io.{File, FileOutputStream}
import scala.util.{Failure, Success, Try}

object InputParser {
  def parse(args: Array[String]): Try[ParsedArgs] = {
    var settings: Try[ParsedArgs] = Success(ParsedArgs(processedImage = None, output = List(), filters = List(), table = None))
    var i = 0
    while (args != null && i < args.length && settings.isSuccess) {
      args(i) match {
        case "--image" =>
          if (settings.get.processedImage.isDefined){
            settings = Failure(new IllegalArgumentException("Only one image can be provided"))
          } else
          if (i + 1 < args.length) {
            val image = args(i + 1).trim
            val extension = image.split('.').last
            settings = extension match {
              case "jpg" | "jpeg" => settings.map(s => s.copy(processedImage = Some(new JpgImageLoader(image))))
              case "png" => settings.map(s => s.copy(processedImage = Some(new PngImageLoader(image))))
              case _ => Failure(new IllegalArgumentException(s"Unsupported image format: $extension"))
            }
            i += 1
          } else {
            settings = Failure(new IllegalArgumentException("No image file specified"))
          }
        case "--image-generated" =>
          if (settings.get.processedImage.isDefined){
            settings = Failure(new IllegalArgumentException("Only one image can be provided"))
          } else
          settings = settings.map(s => s.copy(processedImage = Some(new GeneratedImage())))
        case "--output-console" =>
          if (settings.get.output.isEmpty || !settings.get.output.contains(Console.out)){
            settings = settings.map(s => s.copy(output = s.output :+ Console.out))
          }
          else {
            settings = Failure(new IllegalArgumentException("Only one console output can be requested"))
          }
        case "--output-file" =>
          if (i + 1 < args.length) {
            val path = args(i + 1).trim
            val file = new File(path)
            if (!file.exists) {
              file.createNewFile()
            }
            if (file.canWrite) {
              settings = settings.map(s => s.copy(output = s.output :+ new FileOutputStream(file)))
            } else {
              settings = Failure(new IllegalArgumentException(s"Cannot write to file $path"))
            }
            i += 1
          } else {
            settings = Failure(new IllegalArgumentException("No output file specified"))
          }
        case "--table" =>
            if (settings.get.table.isDefined){
                settings = Failure(new IllegalArgumentException("Only one table can be provided"))
            } else
            if (i + 1 < args.length) {
                val name = args(i + 1).trim
                tableByName(name) match {
                    case Some(table) => settings = settings.map(s => s.copy(table = Some(table)))
                    case None => settings = Failure(new IllegalArgumentException(s"Table $name not found"))
                }
                i += 1
            } else {
                settings = Failure(new IllegalArgumentException("No table name specified"))
            }
        case "--custom-table" =>
            if (settings.get.table.isDefined){
                settings = Failure(new IllegalArgumentException("Only one table can be provided"))
            } else
            if (i + 1 < args.length) {
                val table = args(i + 1)
                settings = settings.map(s => s.copy(table = Some(table)))
                i += 1
            } else {
                settings = Failure(new IllegalArgumentException("No table specified"))
            }
        case _ =>
          Filter.argumentsRequired(args(i)) match {
            case Some(argumentsRequired) =>
              val arguments = args.slice(i + 1, i + 1 + argumentsRequired).toList
              if (arguments.length == argumentsRequired) {
                settings = settings.map(s => s.copy(filters = s.filters :+ Filter(args(i), arguments)))
                i += argumentsRequired
              } else {
                settings = Failure(new IllegalArgumentException(s"Expected $argumentsRequired arguments for filter ${args(i)}"))
              }
            case None =>
              settings = Failure(new IllegalArgumentException(s"Unknown argument: ${args(i)}"))
          }
      }
      i += 1
    }
    settings
  }
}

