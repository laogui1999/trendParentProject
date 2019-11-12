package cn.how2j.trend;
  
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
  
import cn.hutool.core.util.NetUtil;
/**
 *   启动类
 * @author laogui1999
 *
 */
@SpringBootApplication//注解表示它是个启动类
@EnableEurekaServer//表示它是个注册中心服务器
public class EurekaServerApplication {
      
    public static void main(String[] args) {
        //8761 这个端口是默认的，就不要修改了，后面的子项目，都会访问这个端口。
        int port = 8761;
        if(!NetUtil.isUsableLocalPort(port)) {//进行端口号判断，如果这个端口已经被占用了，就给出提示信息。
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }//使用 SpringApplicationBuilder 进行启动 
        new SpringApplicationBuilder(EurekaServerApplication.class).properties("server.port=" + port).run(args);
    }
}