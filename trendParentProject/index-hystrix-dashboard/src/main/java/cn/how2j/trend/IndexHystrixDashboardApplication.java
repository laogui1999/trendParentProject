package cn.how2j.trend;
  
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
   
import cn.hutool.core.util.NetUtil;
/**
 * 断路器监控   
 * @author laogui1999
 *因为TrendTradingBackTestServiceApplication 使用其实是 IndexDataApplication的数据，
 *我们故意关闭IndexDataApplication， 让熔断发生，看看什么效果。
 *可以看到曲线往下走，并且出错很快达到了 100%。
 */
@SpringBootApplication
@EnableHystrixDashboard  //使用 @EnableHystrixDashboard 来启动断路器监控台
public class IndexHystrixDashboardApplication {
    public static void main(String[] args) {
        int port = 8070;
        int eurekaServerPort = 8761;
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
 
        new SpringApplicationBuilder(IndexHystrixDashboardApplication.class).properties("server.port=" + port).run(args);
   
    }
   
}