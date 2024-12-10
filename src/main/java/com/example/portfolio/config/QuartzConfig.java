package com.example.portfolio.config;

import com.example.portfolio.PythonScriptJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail pythonScriptJobDetail() {
        return JobBuilder.newJob(PythonScriptJob.class)
                .withIdentity("pythonScriptJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger pythonScriptJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(pythonScriptJobDetail())
                .withIdentity("pythonScriptTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 6)) // 매일 오전 0시
                .build();
    }
}
