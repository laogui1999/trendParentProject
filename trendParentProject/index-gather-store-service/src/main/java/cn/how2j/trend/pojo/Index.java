package cn.how2j.trend.pojo;
 
import java.io.Serializable;
/**
 * 指数类 
 * @author laogui1999
 *
 */
public class Index implements Serializable{
    String code;//代码
    String name;//名称
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}