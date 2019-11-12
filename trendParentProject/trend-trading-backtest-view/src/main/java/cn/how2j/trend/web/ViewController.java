package cn.how2j.trend.web;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
  
@Controller
@RefreshScope//增加注解：表示允许刷新
public class ViewController {
    @Value("${version}")//增加了个注解来获取版本信息。
    String version;
    @GetMapping("/")
    public String view(Model m) throws Exception {
        m.addAttribute("version", version);//然后用 Model把版本信息丢出去
        return "view";
    }
}
/* 对服务链路追踪的影响 
因为视图服务进行了改造，支持了 rabbitMQ, 那么在默认情况下，它的信息就不会进入 Zipkin了。 在Zipkin 里看不到视图服务的资料了。
为了解决这个问题，在启动 Zipkin 的时候 带一个参数就好了：--zipkin.collector.rabbitmq.addresses=localhost
即本来是
java -jar zipkin-server-2.10.1-exec.jar
现在改成了
java -jar zipkin-server-2.10.1-exec.jar --zipkin.collector.rabbitmq.addresses=localhost
注： 重启 zipkin 后，要再访问业务地址才可以看到依赖关系：
http://127.0.0.1:8031/api-view/*/