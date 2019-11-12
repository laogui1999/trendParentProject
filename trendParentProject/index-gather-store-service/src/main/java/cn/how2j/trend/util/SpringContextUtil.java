package cn.how2j.trend.util;
  
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 *工具类 
 * @author laogui1999
 *用于获取 IndexService. 为什么要用它呢？请看下个步骤的讲解。
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
      
    private SpringContextUtil() {
         System.out.println("SpringContextUtil()");
    }
      
    private static ApplicationContext applicationContext;
      
    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        System.out.println("applicationContext:"+applicationContext);
        SpringContextUtil.applicationContext = applicationContext;
    }
      
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
  
}