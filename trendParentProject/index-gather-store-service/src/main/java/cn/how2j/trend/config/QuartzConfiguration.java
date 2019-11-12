package cn.how2j.trend.config;
 
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import cn.how2j.trend.job.IndexDataSyncJob;
/**
 * 定时器配置类 
 * @author laogui1999
 *定时器配置，每隔一分钟执行一次。 其实每隔一分钟实在太紧密了，4个小时一次就很不错了。 这里用一分钟，是为了便于重复执行的观察效果。
 */
@Configuration
public class QuartzConfiguration {
 
    private static final int interval = 1;
 
    @Bean
    public JobDetail weatherDataSyncJobDetail() {
        return JobBuilder.newJob(IndexDataSyncJob.class).withIdentity("indexDataSyncJob")
        .storeDurably().build();
    }
     
    @Bean
    public Trigger weatherDataSyncTrigger() {
        SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(interval).repeatForever();
         
        return TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
                .withIdentity("indexDataSyncTrigger").withSchedule(schedBuilder).build();
    }
}