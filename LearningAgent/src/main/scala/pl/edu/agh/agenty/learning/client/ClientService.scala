package pl.edu.agh.agenty.learning.client

/**
  * Created by Przemyslaw Kurc on 2017-06-22.
  */

import io.grpc._
import pl.edu.agh.agenty.learning.grpc._
import java.util.concurrent.TimeUnit

import com.google.protobuf.ByteString
import org.slf4j.LoggerFactory
import pl.edu.agh.agenty.learning.utils.Consts

object ClientService {
  private val logger = LoggerFactory.getLogger(classOf[ClientService].getName)

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val client = new ClientService("localhost", 6565)
    client.train()
  }
}

class ClientService {
  import ClientService._
  var channel : ManagedChannel = _
  var blockingStub: LearningAgentGrpc.LearningAgentBlockingStub = _

  def this(host: String, port: Int) {
    this()
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build()
    blockingStub = LearningAgentGrpc.newBlockingStub(channel)
  }

  def shutdown(): Unit = {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }

  def train(): Unit = {
    val value = new Array[Byte](Consts.NUM_PIXELS)
    val labels = new Array[Byte](1)
    var request: TrainBatchRequest = TrainBatchRequest.newBuilder().setCount(1).setLabels(ByteString.copyFrom(labels)).setPixels(ByteString.copyFrom(value)).build()
    var response: TrainResponse = blockingStub.trainBatch(request)
    logger.info("response" + response)
  }
}
