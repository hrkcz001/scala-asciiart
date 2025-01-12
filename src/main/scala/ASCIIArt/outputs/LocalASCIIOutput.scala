package ASCIIArt.outputs

import java.io.OutputStream

class LocalASCIIOutput(stream: OutputStream) extends ASCIIOutput {
  override def write(ascii: String): Unit = {
    stream.write(ascii.getBytes)
    stream.close()
  }
}