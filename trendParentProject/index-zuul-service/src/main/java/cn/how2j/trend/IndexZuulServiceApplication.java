package cn.how2j.trend;
 
import brave.sampler.Sampler;
import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
 
@SpringBootApplication
@EnableZuulProxy//表示这是个网关
@EnableEurekaClient
@EnableDiscoveryClient
public class IndexZuulServiceApplication {
//  http://127.0.0.1:8031/api-codes/codes
//  http://127.0.0.1:8031/api-backtest/simulate/000300
//  http://127.0.0.1:8031/api-view/
    public static void main(String[] args) {
        int port = 8031;
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexZuulServiceApplication.class).properties("server.port=" + port).run(args);
  
    }
 
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
 
}