package cn.how2j.trend;
 
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
/**
 * 启动类
 * @author laogui1999
 *第三方的数据一般说来每天都会更新。 按照前面的做法，需要访问 fresh 地址才能实现刷新的效果，难道每天都自己手动去刷新吗？ 这也很繁琐嘛，何况很有可能会忘记。
	所以我们就引入定时器工具 Quartz 来解决这个问题吧
 */
@SpringBootApplication
@EnableEurekaClient
@EnableHystrix//启动断路器
@EnableCaching//表示启动缓存
 
public class IndexGatherStoreApplication {
    public static void main(String[] args) {
 
        int defaultPort = 8001;
        int port =defaultPort;
        int redisPort = 6379;//用于判断 redis 服务器是否启动
        int eurekaServerPort = 8761;
 
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
 
        if(NetUtil.isUsableLocalPort(redisPort)) {
            System.err.printf("检查到端口%d 未启用，判断 redis 服务器没有启动，本服务无法使用，故退出%n", redisPort );
            System.exit(1);
        }
 
        if(null!=args && 0!=args.length) {
            for (String arg : args) {
                if(arg.startsWith("port=")) {
                    String strPort= StrUtil.subAfter(arg, "port=", true);
                    if(NumberUtil.isNumber(strPort)) {
                        port = Convert.toInt(strPort);
                    }
                }
            }
        }       
         
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexGatherStoreApplication.class).properties("server.port=" + port).run(args);
         
    }
     //声明了restTemplate，IndexService 才能够使用 RestTemplate
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}