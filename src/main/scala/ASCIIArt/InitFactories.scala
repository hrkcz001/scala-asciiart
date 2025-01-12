package ASCIIArt

def initFactoriesObjects(): Unit = {
  ASCIIArt.filters.InvertFilterFactory
  ASCIIArt.filters.RotateFilterFactory
  ASCIIArt.filters.ScaleFilterFactory
  
  ASCIIArt.imageLoaders.JpgImageLoaderFactory
  ASCIIArt.imageLoaders.PngImageLoaderFactory
  
  ASCIIArt.converters.DefaultASCIIConverterFactory
  ASCIIArt.converters.ExtendedASCIIConverterFactory
  ASCIIArt.converters.NonlinearASCIIConverterFactory
}