package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

import java.io.File
import scala.util.{Failure, Success, Try}

class PngImageLoader(file: File, mimeDetector: MimeDetector, pixelReader: PixelReader) extends LocalImageLoader(file, mimeDetector) {

  private val isPng = if (mime != "image/png") {
    val path = file.getPath
    Failure(new IllegalArgumentException(s"File $path is not a PNG file"))
  } else {
    Success(true)
  }
  
  private val pixels = if (isPng.isSuccess) pixelReader.read(file) else List[List[Pixel]]()

  override def getPixels: Try[List[List[Pixel]]] = wasFound.flatMap(_ => isPng.map(_ => pixels))
}

object PngImageLoaderFactory extends ImageLoaderFactory {
  private var mimeDetector: MimeDetector = TikaMimeDetector
  private var pixelReader: PixelReader = DefaultPixelReader
  
  def set(mimeDetector: MimeDetector): Unit = this.mimeDetector = mimeDetector
  def set(pixelReader: PixelReader): Unit = this.pixelReader = pixelReader
  
  private[imageLoaders] def getMimeDetector: MimeDetector = mimeDetector
  private[imageLoaders] def getPixelReader: PixelReader = pixelReader
  
  override def apply(path: String): ImageLoader = new PngImageLoader(new File(path), mimeDetector, pixelReader)
  
  ImageLoaders.register("png", this)
}
