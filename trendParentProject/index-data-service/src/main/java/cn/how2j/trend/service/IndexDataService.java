package cn.how2j.trend.service;
 
import java.util.List;
 
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
 
import cn.how2j.trend.pojo.IndexData;
import cn.hutool.core.collection.CollUtil;
/**
 * 服务类 
 * @author laogui1999
 *这个和 IndexService 是对应的，只是这里获取的是指数数据。
需要注意的是，指数数据存放的key 是 indexData-code-000300 这种风格
 */
@Service
@CacheConfig(cacheNames="index_datas")
public class IndexDataService {
 
    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }
}