package ASCIIArt

import ASCIIArt.Pixel.grayscale
import ASCIIArt.filters.{Filter, Filters}
import ASCIIArt.imageLoaders.{GeneratedImage, ImageLoaders}
import ASCIIArt.converters.{ASCIIConverter, ASCIIConverters, LinearASCIIConverter}
import ASCIIArt.outputs.{ASCIIOutput, LocalASCIIOutput}

import java.io.{File, FileOutputStream, IOException}
import scala.util.{Failure, Success, Try}

@main def main(args: String*): Unit = {
  //loading factories to let them register themselves
  initFactoriesObjects()

  //parsing input arguments
  InputParser.parse(args.toArray).flatMap(parsedArgs =>
    (parsedArgs.processedImage match {
      case Some(Some(path)) =>
        ImageLoaders.getLoaderFactory(path.split('.').last) match {
          case Some(loaderFactory) => Success(loaderFactory.apply(path))
          case None => Failure(new IllegalArgumentException("Unsupported file type"))
        }
      case Some(None) => Success(new GeneratedImage())
      case None => Failure(new IllegalArgumentException("No image provided"))
    }).flatMap(processedImage =>

      //outputs preparation
      (Try{
        var outputs = List[ASCIIOutput]()
        for (outputPath <- parsedArgs.output) {
          val file = new File(outputPath)
          if (!file.exists()) file.createNewFile()
          outputs :+= new LocalASCIIOutput(new FileOutputStream(file))
        }
        if (parsedArgs.console) outputs :+= new LocalASCIIOutput(System.out)
        if (outputs.isEmpty) throw new IllegalArgumentException("No output specified")
        else outputs
      } match {
        case Success(outputs) => Success(outputs)
        case Failure(e: SecurityException) => Failure(new IllegalArgumentException("No permission to create output file"))
        case Failure(e: IOException) => Failure(new IllegalArgumentException("Error creating output file"))
        case Failure(e) => Failure(e)
      }).flatMap(outputs =>

        //filters preparation
        val filters: List[Try[Filter]] = parsedArgs.filters.map((name, args) =>
          Filters.getFilterFactory(name).map(factory =>
            factory.apply(args)).getOrElse(
              Failure(new IllegalArgumentException(s"Filter $name not found"))
            )
          )
        (if (filters.exists(_.isFailure)) {
          val errors = filters.collect({ case Failure(e: IllegalArgumentException) => e.getMessage })
          Failure(new IllegalArgumentException(errors.mkString(System.lineSeparator())))
        } else {
          Success(filters.map(_.get))
        }).flatMap(filters =>

          //table preparation
          (parsedArgs.table match {
            case Some(Left(name)) => ASCIIConverters.getConverterFactory(name).map(_.apply()) match {
              case Some(converter) => Success(converter)
              case None => Failure(new IllegalArgumentException(s"Table $name not found"))
            }
            case Some(Right(customTable)) => Success(new LinearASCIIConverter(customTable))
            case None => Success(ASCIIConverters.getConverterFactory("default").get.apply())
          }).flatMap(table =>

            //getting pixels from image
            processedImage.getPixels.flatMap(pixels =>
              //transforming pixels to grayscale
              val grayscalePixels = pixels.map(_.map(pixel => grayscale(pixel)))
              //applying filters to grayscale image
              filters.foldLeft(GrayscaleImage(grayscalePixels))
                ((image, filter) => image.flatMap(filter.applyFilter))
            ).map(finalImage =>
              //converting grayscale image to ASCII
              val result = table.convert(finalImage)
              //writing result to outputs
              outputs.foreach(_.write(result))
            )
          )
        )
      )
    )
  ).recover({
    case e: IllegalArgumentException => System.err.println(e.getMessage)
    case e: Throwable => System.err.println("An error occurred")
  })
}