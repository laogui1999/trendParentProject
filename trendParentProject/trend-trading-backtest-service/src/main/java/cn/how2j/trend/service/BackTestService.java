package cn.how2j.trend.service;
 
import cn.how2j.trend.client.IndexDataClient;
import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.*;

/**
 * 提供所有模拟回测数据的微服务
 * @author laogui1999
 *
 */
@Service
public class BackTestService {
    @Autowired IndexDataClient indexDataClient;
 
    public List<IndexData> listIndexData(String code){
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);
 
//      for (IndexData indexData : result) {
//          System.out.println(indexData.getDate());
//      }
 
        return result;
    }
    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {
 
        List<Profit> profits =new ArrayList<>();
        //增加一个 集合
        List<Trade> trades = new ArrayList<>();
 
        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        float value = 0;
 
        int winCount = 0;
        float totalWinRate = 0;
        float avgWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;
        float avgLossRate = 0;
 
        float init =0;
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();
 
        for (int i = 0; i<indexDatas.size() ; i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i,ma,indexDatas);
            float max = getMax(i,ma,indexDatas);
 
            float increase_rate = closePoint/avg;
            float decrease_rate = closePoint/max;
 
            if(avg!=0) {
                //buy 超过了均线
                if(increase_rate>buyRate  ) {
                    //如果没买
                    if(0 == share) {
                        share = cash / closePoint;
                        cash = 0;
                        //购买的时候创建一个交易对象
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                //sell 低于了卖点
                else if(decrease_rate<sellRate ) {
                    //如果没卖
                    if(0!= share){
                        cash = closePoint * share * (1-serviceCharge);
                        share = 0;
                        //出售的时候，修改前面创建的这个交易对象
                        Trade trade =trades.get(trades.size()-1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
 
                        float rate = cash / initCash;
                        trade.setRate(rate);
                        //在出售的时候记录这些信息
                        if(trade.getSellClosePoint()-trade.getBuyClosePoint()>0) {
                            totalWinRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        }
 
                        else {
                            totalLossRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount ++;
                        }
                    }
                }
                //do nothing
                else{
 
                }
            }
 
            if(share!=0) {
                value = closePoint * share;
            }
            else {
                value = cash;
            }
            float rate = value/initCash;
 
            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate*init);
             
            profits.add(profit);
 
        }
        //在交易全部结束之后，计算平均盈利或者亏损利率
        avgWinRate = totalWinRate / winCount;
        avgLossRate = totalLossRate / lossCount;
        //调用上面的方法，并把结果返回
        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);
 
        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);//最后把这个交易集合返回
 
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
 
        map.put("annualProfits", annualProfits);
        return map;
    }
 
    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i-1-day;
        if(start<0)
            start = 0;
        int now = i-1;
 
        if(start<0)
            return 0;
 
        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }
 
    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i-1-ma;
        int now = i-1;
 
        if(start<0)
            return 0;
 
        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }
    //增加一个 getYear 方法， 获取某个日期如 2019-05-21 里的年份：
    //增加 getYear，用于计算当前的时间范围是多少年。
    public float getYear(List<IndexData> allIndexDatas) {
        float years;
        String sDateStart = allIndexDatas.get(0).getDate();
        String sDateEnd = allIndexDatas.get(allIndexDatas.size()-1).getDate();
 
        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);
 
        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days/365f;
        return years;
    }
    //计算完整时间范围内，每一年的指数投资收益和趋势投资收益
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
 
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
 
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
 
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
 
            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
 
        }
        return result;
    }
    //计算某一年的的指数收益
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;
 
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
//          Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);
 
            if(currentYear == year) {
                if(null==first)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }
    //计算某一年的趋势投资收益
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
 
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
 
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }
}