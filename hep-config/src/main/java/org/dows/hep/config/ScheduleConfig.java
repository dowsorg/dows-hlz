//package org.dows.hep.config;
//
//import cn.hutool.core.util.StrUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.support.CronTrigger;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Slf4j
//@Configuration
//@EnableScheduling
//public class ScheduleConfig implements SchedulingConfigurer {
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        /*taskRegistrar.addFixedDelayTask(() -> {
//            // 定时任务的具体逻辑
//        }, 1000);*/
//        taskRegistrar.addTriggerTask(
//                //1.添加任务内容(Runnable)
//                () -> System.out.println("执行动态定时任务: " + LocalDateTime.now().toLocalTime()),
//                //2.设置执行周期(Trigger)
//                triggerContext -> {
//                    //2.1 从数据库获取执行周期
//                    String cron = "0/5 * * * * ?";
//                    //2.2 合法性校验.
//                    if (StrUtil.isEmpty(cron)) {
//                        // Omitted Code ..
//                    }
//                    //2.3 返回执行周期(Date)
//                    Instant instant = new CronTrigger(cron).nextExecution(triggerContext);
//                    return instant;
//                }
//        );
//        taskRegistrar.setScheduler(taskScheduler());
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.setPoolSize(10);
//        taskScheduler.setThreadNamePrefix("hep-taskScheduler-");
//        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
//        return taskScheduler;
//
//    }
//
//    /**
//     * 线程池 执行定时定时任务
//     *
//     * @return
//     */
//    @Bean("taskExecutor")
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        //表示线程池核心线程，正常情况下开启的线程数量。
//        executor.setCorePoolSize(3);
//        //配置队列大小
//        executor.setQueueCapacity(2);
//        //当核心线程都在跑任务，还有多余的任务会存到此处。
//        executor.setMaxPoolSize(5);
//        //非核心线程的超时时长，超长后会被回收。
//        executor.setKeepAliveSeconds(60);
//        //配置线程池前缀
//        executor.setThreadNamePrefix("HepThreadPool-");
//        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//        //配置拒绝策略
//        executor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor exe) -> {
//            log.warn("HepThreadPool-当前任务线程池队列已满!");
//        });
//        //初始化线程池
//        executor.initialize();
//        return executor;
//
//    }
//}
