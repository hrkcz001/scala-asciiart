package ASCIIArt

import ASCIIArt.ASCIIConverter.{convertToASCII, linearTable, tableByName}
import ASCIIArt.Filter.applyFilter
import ASCIIArt.GrayscaleImage.process
import ASCIIArt.Pixel.grayscale
import ASCIIArt.imageLoaders.{GeneratedImage, ProcessedImage}

import java.io.OutputStream

case class ParsedArgs( processedImage: Option[ProcessedImage]
                     , output: List[OutputStream]
                     , filters: List[Filter]
                     , table: Option[String])

@main def main(args: String*): Unit = {
  InputParser.parse(args.toArray).flatMap(parsedArgs =>
      // If no image is provided, use a generated image
      val processedImage = parsedArgs.processedImage.getOrElse(new GeneratedImage())
      // If no output is provided, use the console
      val output = if (parsedArgs.output.isEmpty) List(Console.out) else parsedArgs.output
      // If no table is provided, use the default table
      val table = parsedArgs.table.getOrElse(tableByName("linear").get)

      processedImage.getPixels.flatMap(pixels =>
          val grayscalePixels = pixels.map(_.map(pixel => grayscale(pixel)))
          parsedArgs.filters.foldLeft(GrayscaleImage(grayscalePixels))
            ((image, filter) => image.flatMap(i => applyFilter(i, filter)))
            .map(finalImage =>
              convertToASCII(process(finalImage), linearTable(table))
                .foreach(line => output.foreach(_.write((line + System.lineSeparator()).getBytes)))
            )
      )
  ).recover({
    case e: IllegalArgumentException => System.err.println(e.getMessage)
    case e: Throwable => System.err.println("An error occurred")
  })
}
