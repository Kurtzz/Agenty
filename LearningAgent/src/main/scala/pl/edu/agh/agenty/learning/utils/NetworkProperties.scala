package pl.edu.agh.agenty.learning.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
  * Created by P on 08.06.2017.
  */
@Component
@ConfigurationProperties("network")
class NetworkProperties {
  var _batchSize = 0
  var _randomSeed = 0
  var _numEpochs = 0

  def batchSize: Int = _batchSize
  def batchSize_(value: Int): Unit = _batchSize = value

  def randomSeed: Int = _randomSeed
  def randomSeed_(value: Int): Unit = _randomSeed = value

  def numEpochs: Int = _numEpochs
  def numEpochs_(value: Int): Unit = _numEpochs = value
}