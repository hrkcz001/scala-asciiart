package ASCIIArt.imageLoaders

import java.io.File
import org.apache.tika.Tika

import scala.util.{Try, Failure, Success}

abstract class LocalImageLoader(path: String) extends ProcessedImage {
  protected val file: File = new File(path)
  protected val mime: String = try { new Tika().detect(file) } catch { case _: Throwable => "" }
  protected val wasFound: Try[Boolean] = if (!file.canRead) {
    Failure(new IllegalArgumentException(s"File $path not found"))
  } else {
    Success(true)
  }
}