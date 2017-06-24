package pl.edu.agh.agenty.learning.service

import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.nd4j.linalg.api.ndarray.INDArray
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import pl.edu.agh.agenty.learning.exception.WrongNumberOfPixelsException
import pl.edu.agh.agenty.learning.grpc._
import pl.edu.agh.agenty.learning.neural.NeuralNetworkManager
import pl.edu.agh.agenty.learning.utils.LearningAgentUtils

import scala.collection.JavaConverters
import scala.collection.mutable.ListBuffer

/**
  * Created by P on 08.06.2017.
  */
@GRpcService
class LearningAgentService extends LearningAgentGrpc.LearningAgentImplBase {
  private val log : Logger = LoggerFactory.getLogger(this.getClass)

  @Autowired var networkManager : NeuralNetworkManager = _
  @Autowired var learningAgentUtils : LearningAgentUtils = _

  override def trainBatch(request: TrainBatchRequest, responseObserver: StreamObserver[TrainResponse]): Unit = {
    val iterator = learningAgentUtils.createDataSetIterator(request.getCount, request.getPixels, request.getLabels)
    networkManager.trainNetwork(iterator)
    responseObserver.onNext(TrainResponse.getDefaultInstance)
    responseObserver.onCompleted()
  }

  override def classifyImageProb(request: ClassifyRequest, responseObserver: StreamObserver[ClassifyProbResponse]): Unit = {
    val input = learningAgentUtils.createINDArray(request.getPixels)
    val result = networkManager.classifyDigit(input)

    if (result == null) {
      responseObserver.onError(new WrongNumberOfPixelsException())
      return
    }

    val probabilities = new ListBuffer[java.lang.Float]

    (0 until result.length()).foreach(i => probabilities += result.getFloat(i))

    val response = ClassifyProbResponse.newBuilder()
      .addAllResults(JavaConverters.asJavaIterable(probabilities)).build()
    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }


  override def classifyImageSoftmax(request: ClassifyRequest , responseObserver: StreamObserver[ClassifyConcreteResponse]): Unit = {
    val input = learningAgentUtils.createINDArray(request.getPixels)
    val output = networkManager.classifyDigit(input)

    val result: Int = getDigitWithHighestProbability(output)

    if (result < 0) {
      log.error("Max probability is not a positive number.")
    }

    val response = ClassifyConcreteResponse.newBuilder().setResult(result).build()
    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override def reset(request: ResetRequest, responseObserver: StreamObserver[ResetResponse]): Unit = {
    networkManager.resetNetwork()

    responseObserver.onNext(ResetResponse.getDefaultInstance)
    responseObserver.onCompleted()
  }

  private def getDigitWithHighestProbability(output: INDArray): Int = {
    var maxIndex = -1
    var maxProbability = 0f
    (0 until output.length).foreach(i => {
      if (output.getFloat(i) > maxProbability) {
        maxIndex = i
        maxProbability = output.getFloat(i)
      }
    })

    maxIndex
  }
}
