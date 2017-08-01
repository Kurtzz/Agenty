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

import io.grpc.ServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

//remove if not needed

@Component
@Configuration
class MyGRpcServerBuilderConfigurer extends GRpcServerBuilderConfigurer {
  override def configure(serverBuilder: ServerBuilder[_]): Unit = {
    val myServerBuilder: NettyServerBuilder =
      serverBuilder.asInstanceOf[NettyServerBuilder]
    serverBuilder.asInstanceOf[NettyServerBuilder].maxMessageSize(2000000000)
  }
}
