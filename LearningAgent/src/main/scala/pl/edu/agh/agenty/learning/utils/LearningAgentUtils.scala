package pl.edu.agh.agenty.learning.utils

import com.google.protobuf.ByteString
import org.deeplearning4j.berkeley.Pair
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.JavaConverters
import scala.collection.mutable.ListBuffer

/**
  * Created by P on 08.06.2017.
  */
@Component
class LearningAgentUtils

object LearningAgentUtils {
  @Autowired
  private var properties: NetworkProperties = _

  def createDataSetIterator(count: Int, pixels: ByteString, labels: ByteString): DataSetIterator = {
    var pairs = new ListBuffer[Pair[INDArray, INDArray]]()
    for(i <- 0 until count) {
      val image = pixels.substring(i * Consts.NUM_PIXELS, (i + 1) * Consts.NUM_PIXELS).toByteArray
      val label = labels.byteAt(i)
      val pair = new Pair[INDArray, INDArray](createINDArray(image), createINDArrayLabel(label))
      pairs += pair
    }
    new INDArrayDataSetIterator(JavaConverters.asJavaCollection(pairs), properties.batchSize)
  }

  def createINDArray(pixels: ByteString): INDArray = createINDArray(pixels.toByteArray)

  def createINDArray(pixels: Array[Byte]): INDArray = {
    var indArray = Nd4j.create(Consts.NUM_PIXELS)
    for(i <- 0 until Consts.NUM_PIXELS) {
      indArray = indArray.put(0, i, pixels(i))
    }
    indArray
  }

  private def createINDArrayLabel(i: Byte) = Nd4j.create(10).put(0, i, 1)
}
