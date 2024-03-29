package cn.how2j.trend.util;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
/**
 * 工具类 
 * @author laogui1999
 *因为要访问的时候，才能看到监控信息，所以做个工具类，不断地访问。
 */
public class AccessService {
    public static void main(String[] args) {
    	 
    	//断路器监控
    	/*while(true) {
             ThreadUtil.sleep(1000);
             try {
                 String html= HttpUtil.get("http://127.0.0.1:8051/simulate/399975/20/1.01/0.99/0/null/null/");
                 System.out.println("html length:" + html.length());
             }
             catch(Exception e) {
                 System.err.println(e.getMessage());
             }
   
         }*/
    	//断路器聚合监控
    	while(true) {
            ThreadUtil.sleep(1000);
            access(8051);
            access(8052);
        }
    }
 
    public static void access(int port) {
        try {
            String html= HttpUtil.get(String.format("http://127.0.0.1:%d/simulate/399975/20/1.01/0.99/0/null/null/",port));
            System.out.printf("%d 地址的模拟回测服务访问成功，返回大小是 %d%n" ,port, html.length());
        }
        catch(Exception e) {
            System.err.printf("%d 地址的模拟回测服务无法访问%n",port);
        }
    }
}