package pl.edu.agh.agenty.learning.neural

import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.conf.{NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.edu.agh.agenty.learning.utils.{Consts, NetworkProperties}

@Service
class NeuralNetworkManager {
  import NeuralNetworkManager._

  private var network: MultiLayerNetwork = _

  def this(properties: NetworkProperties) {
    this()
    this.network = buildNetwork
  }

  def trainNetwork(iterator: DataSetIterator): Unit = {
    for (i <- 0 to properties.numEpochs) {
      log.info("Training network (epoch: {}) ...", i + 1)
      network.fit(iterator)
    }
  }

  def classifyDigit(input: INDArray): INDArray = {
    log.info("Classifying digit...")
    if (input.length != Consts.NUM_PIXELS) {
      log.error("Wrong number of pixels: {}", input.length)
      return null
    }
    val result = network.output(input)
    log.info("Digit classification: " + result)
    result
  }

  def resetNetwork(): Unit = {
    network = buildNetwork
//    network clear()
  }
}

object NeuralNetworkManager {
  private val log = LoggerFactory.getLogger(classOf[NeuralNetworkManager])
  @Autowired private var properties: NetworkProperties = _
  private val configuration = new NeuralNetConfiguration.Builder()
    .seed(properties.randomSeed)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .iterations(1)
    .learningRate(0.006)
    .updater(Updater.NESTEROVS).momentum(0.9)
    .regularization(true).l2(1e-4)
    .list.layer(0, new DenseLayer.Builder()
    .nIn(Consts.NUM_PIXELS)
    .nOut(1000)
    .activation(Activation.RELU)
    .weightInit(WeightInit.XAVIER)
    .build)
    .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .nIn(1000)
      .nOut(Consts.OUTPUT_NUM)
      .activation(Activation.SOFTMAX)
      .weightInit(WeightInit.XAVIER)
      .build)
    .pretrain(false)
    .backprop(true)
    .build

  private def buildNetwork: MultiLayerNetwork = {
    log.info("Building multilayer network...")
    val model = new MultiLayerNetwork(configuration)
    model.setListeners(new ScoreIterationListener(10))
    model.init()
    model
  }
}
