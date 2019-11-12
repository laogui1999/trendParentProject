package cn.how2j.trend;
  
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
  
import cn.hutool.core.util.NetUtil;
/**
 * 断路器聚合监控Turbine
 * @author laogui1999
 *启动类，注意这里用的是 8080端口，可能和已经存在的端口有冲突。
 *微服务通常会是多个实例组成的一个集群。 倘若集群里的实例比较多，难道要挨个挨个去监控这些实例吗？
 *何况有时候，根据集群的需要，会动态增加或者减少实例，监控起来就更麻烦了。
 *所以为了方便监控集群里的多个实例，springCloud 提供了一个 turbine 项目，
 *它的作用是把一个集群里的多个实例汇聚在一个 turbine里，这个然后再在 断路器监控里查看这个 turbine, 这样就能够在集群层面进行监控啦。
 *
 */
@SpringBootApplication
@EnableTurbine
public class IndexTurbineApplication {
    public static void main(String[] args) {
        int port = 8080;
        int eurekaServerPort = 8761;
 
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexTurbineApplication.class).properties("server.port=" + port).run(args);
  
    }
  
}