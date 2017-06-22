package pl.edu.agh.agenty.learning

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
  * Created by P on 08.06.2017.
  */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(Array("pl.edu.agh.agenty.learning"))
class Config

object Application extends App {
  SpringApplication.run(classOf[Config])
}
