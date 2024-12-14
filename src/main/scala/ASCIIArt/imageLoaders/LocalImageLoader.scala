package ASCIIArt.imageLoaders

import java.io.File
import jakarta.activation.MimetypesFileTypeMap

abstract class LocalImageLoader(path: String) extends ProcessedImage {
  protected val file: File = new File(path)
  protected val mime: String = new MimetypesFileTypeMap().getContentType(file)
  if (mime == "application/octet-stream") {
    throw new Exception("File not found")
  }
}