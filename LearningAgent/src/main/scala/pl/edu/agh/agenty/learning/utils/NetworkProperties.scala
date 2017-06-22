package pl.edu.agh.agenty.learning.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
  * Created by P on 08.06.2017.
  */
@Component
@ConfigurationProperties("network")
class NetworkProperties {
  var batchSize: Int = _
  var randomSeed: Int = _
  var numEpochs: Int = _

  def setBatchSize(value: Int): Unit = {
    batchSize = value
  }

  def setRandomSeed(value: Int): Unit = {
    randomSeed = value
  }

  def setNumEpochs(value: Int): Unit = {
    numEpochs = value
  }
}