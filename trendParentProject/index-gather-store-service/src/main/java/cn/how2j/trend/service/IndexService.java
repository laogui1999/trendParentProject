package cn.how2j.trend.service;
 
import cn.how2j.trend.pojo.Index;
import cn.how2j.trend.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
@Service
@CacheConfig(cacheNames="indexes")//表示缓存的名称是 indexes. 如图所示，保存到 redis 就会以 indexes 命名
public class IndexService {
    private List<Index> indexes;
    @Autowired RestTemplate restTemplate;
    /*刷新数据。 刷新的思路就是：
		4.1 先运行 fetch_indexes_from_third_part 来获取数据
		4.2 删除数据
		4.3 保存数据
		从而达到刷新的效果*/
    //表示如果fetch_indexes_from_third_part获取失败了，就自动调用 third_part_not_connected 并返回。
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        indexes =fetch_indexes_from_third_part();
        //SpringContextUtil.getBean从已经存在的方法里调用 redis 相关方法，并不能触发 redis 相关操作，所以只好用这种方式重新获取一次了。
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }
    //清空数据
    @CacheEvict(allEntries=true)
    public void remove(){
 
    }
    //保存数据，这个专门用来往 redis 里保存数据，注意： 这个 indexes 是一个成员变量。
    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        System.out.println(this);
        return indexes;
    }
    //获取数据，这个就是专门用来从 redis 中获取数
    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        return CollUtil.toList();
    }
    //方法上增加： @Cacheable(key="'all_codes'")表示保存到 redis 用的 key 就会 是all_codes.
    public List<Index> fetch_indexes_from_third_part(){
    	//使用工具类RestTemplate 来获取如下地址：
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json",List.class);
        return map2Index(temp);
    }
    private List<Index> map2Index(List<Map> temp) {
        List<Index> indexes = new ArrayList<>();
        for (Map map : temp) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index= new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }
 
        return indexes;
    }
    //自己创建个指数对象，然后返回集合。
    public List<Index> third_part_not_connected(){
        System.out.println("third_part_not_connected()");
        Index index= new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }
 
}