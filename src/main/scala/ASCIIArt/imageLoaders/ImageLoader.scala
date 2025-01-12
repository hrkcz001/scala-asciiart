package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

import scala.util.Try

trait ImageLoader {
  def getPixels: Try[List[List[Pixel]]]
}

trait ImageLoaderFactory {
  def apply(path: String): ImageLoader
}

object ImageLoaders {
  private var loaders: Map[String, ImageLoaderFactory] = Map()

  def register(extension: String, loader: ImageLoaderFactory): Unit = {
    loaders += (extension -> loader)
  }

  def getLoaderFactory(extension: String): Option[ImageLoaderFactory] = {
    loaders.get(extension)
  }
}