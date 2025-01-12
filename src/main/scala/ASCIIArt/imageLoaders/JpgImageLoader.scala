package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

import java.io.File
import scala.util.{Failure, Success, Try}

class JpgImageLoader(file: File, mimeDetector: MimeDetector, pixelReader: PixelReader) extends LocalImageLoader(file, mimeDetector) {
  
  private val isJpeg = if (mime != "image/jpeg") {
    val path = file.getPath
    Failure(new IllegalArgumentException(s"File $path is not a JPEG file"))
  } else {
    Success(true)
  }
  
  private val pixels = if (isJpeg.isSuccess) pixelReader.read(file) else List[List[Pixel]]()

  override def getPixels: Try[List[List[Pixel]]] = wasFound.flatMap(_ => isJpeg.map(_ => pixels))
}

object JpgImageLoaderFactory extends ImageLoaderFactory {
  private var mimeDetector: MimeDetector = TikaMimeDetector
  private var pixelReader: PixelReader = DefaultPixelReader

  def set(mimeDetector: MimeDetector): Unit = this.mimeDetector = mimeDetector
  def set(pixelReader: PixelReader): Unit = this.pixelReader = pixelReader

  private[imageLoaders] def getMimeDetector: MimeDetector = mimeDetector
  private[imageLoaders] def getPixelReader: PixelReader = pixelReader
  
  override def apply(path: String): ImageLoader = new JpgImageLoader(new File(path), mimeDetector, pixelReader)

  ImageLoaders.register("jpg", this)
  ImageLoaders.register("jpeg", this)
}