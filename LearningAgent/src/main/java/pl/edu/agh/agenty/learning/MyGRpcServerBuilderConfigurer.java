package pl.edu.agh.agenty.learning;

import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class MyGRpcServerBuilderConfigurer extends GRpcServerBuilderConfigurer {
    @Override
    public void configure(ServerBuilder<?> serverBuilder) {
        NettyServerBuilder myServerBuilder = (NettyServerBuilder) serverBuilder;
        ((NettyServerBuilder) serverBuilder).maxMessageSize(2000000000);
    }
}
