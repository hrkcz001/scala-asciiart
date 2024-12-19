package ASCIIArt.imageLoaders

import ASCIIArt.Pixel

import java.util.Random
import scala.util.{Success, Try}

val MIN_HEIGHT = 10
val MAX_HEIGHT = 100
val MAX_RATIO = 2.0f

class GeneratedImage( minHeight: Int = MIN_HEIGHT
                    , maxHeight: Int = MAX_HEIGHT
                    , maxRatio: Float = MAX_RATIO) extends ProcessedImage {
  
  private val random = new Random()
  
  // max is here to avoid bugs even if constants are changed e.g. set from conf or cli(current implementation doesn't support it)
  private val height = math.max(1, random.nextInt(1 + maxHeight - minHeight) + minHeight)
  private val width = math.max(1, random.nextInt(1 + (height * maxRatio).toInt - (height / maxRatio).toInt) + (height / maxRatio).toInt)
  
  override def getPixels: Try[List[List[Pixel]]] =
    Success(List.fill(height)(List.fill(width)(Pixel(random.nextInt(256), random.nextInt(256), random.nextInt(256)).get)))
}
