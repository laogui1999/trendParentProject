package cn.how2j.trend.web;
 
import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.how2j.trend.service.BackTestService;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.*;

/**
 * 控制器
 * @author laogui1999
 *返回的数据是放在一个 Map 里的，而目前的key是 indexDatas。
	因为将来会返回各种各样的数据，通过这种方式才好区分不同的数据。
 */
@RestController
public class BackTestController {
    @Autowired BackTestService backTestService;
    
    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(
            @PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold//增加购买阈值参数
            ,@PathVariable("sellThreshold") float sellThreshold//增加出售阈值参数
            ,@PathVariable("serviceCharge") float serviceCharge//获取此参数
            ,@PathVariable("startDate") String strStartDate//参数可以接受开始日期
            ,@PathVariable("endDate") String strEndDate//参数可以接受结束日期
    ) throws Exception {
    	//根据开始日期和结束日期获取对应日期范围的数据
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        //计算出开始日期和结束日期
        String indexStartDate = allIndexDatas.get(0).getDate();
        String indexEndDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();
 
        allIndexDatas = filterByDateRange(allIndexDatas,strStartDate, strEndDate);
        //使用这两个参数(购买和出售阈值参数)
        float sellRate = sellThreshold;
        float buyRate = buyThreshold;
        
        Map<String,?> simulateResult= backTestService.simulate(ma,sellRate, buyRate,serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");
        // 获取年份
        float years = backTestService.getYear(allIndexDatas);
        //计算指数的收益和趋势投资的收益，以及对应的年化收益率。
        float indexIncomeTotal = (allIndexDatas.get(allIndexDatas.size()-1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;
 
        int winCount = (Integer) simulateResult.get("winCount");
        int lossCount = (Integer) simulateResult.get("lossCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");
        //获取结果，并返回
        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");
 
        Map<String,Object> result = new HashMap<>();
        result.put("indexDatas", allIndexDatas);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        result.put("trades", trades);//取出并返回这个交易集合
        result.put("years", years);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);
 
        result.put("winCount", winCount);
        result.put("lossCount", lossCount);
        result.put("avgWinRate", avgWinRate);
        result.put("avgLossRate", avgLossRate);
 
        result.put("annualProfits", annualProfits);
 
        return result;
    }
    // filterByDateRange 方法
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return allIndexDatas;
 
        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
 
        for (IndexData indexData : allIndexDatas) {
            Date date =DateUtil.parse(indexData.getDate());
            if(
                    date.getTime()>=startDate.getTime() &&
                            date.getTime()<=endDate.getTime()
                    ) {
                result.add(indexData);
            }
        }
        return result;
    }
 
}