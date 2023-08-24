package org.dows.hep.event;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.calc.CalculatorDispatcher;
import org.dows.hep.biz.noticer.PeriodEndNoticer;
import org.dows.hep.biz.noticer.PeriodStartNoticer;
import org.dows.hep.biz.schedule.TaskScheduler;
import org.dows.hep.biz.task.ExperimentRestartTask;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;
import org.dows.hep.websocket.HepClientMonitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Date;

/**
 * 应用重启事件，拉起任务及执行任务
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationRestartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${spring.application.name}")
    public String appName;

    @Value("${spring.profiles.active}")
    public String appEnv;

    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final ExperimentInstanceService experimentInstanceService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TaskScheduler taskScheduler;

    private final ExperimentTimerBiz experimentTimerBiz;

    private final CalculatorDispatcher calculatorDispatcher;

    private final PeriodStartNoticer periodStartNoticer;

    private final PeriodEndNoticer periodEndNoticer;

    private final ExperimentSettingBiz experimentSettingBiz;

    private final ExperimentSchemeBiz experimentSchemeBiz;

    // 启动监听
    private final HepClientMonitor hepClientMonitor;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.setProperty("appName", appName);
        System.setProperty("appEnv", appEnv);
        /*String podIp = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();*/
        String podIp = InetAddress.getLocalHost().getHostAddress();
        if (!StrUtil.isBlank(podIp)) {
            System.setProperty("podIp", podIp);
        } else {
            System.setProperty("podIp", "0.0.0.0");
        }
        log.info("执行任务重启......");
        try {
            hepClientMonitor.start();
            String appId = "3";
            Date now = DateUtil.date();
            // 更新重启时间(当前应用下大于当前时间且未执行的任务)
            experimentTaskScheduleService.lambdaUpdate()
                    .set(ExperimentTaskScheduleEntity::getRestartTime, now)
                    .eq(ExperimentTaskScheduleEntity::getExecuted, false)
                    .eq(ExperimentTaskScheduleEntity::getAppId, appId)
                    .gt(ExperimentTaskScheduleEntity::getExecuteTime, now)
                    .update();
            ExperimentRestartTask experimentRestartTask = new ExperimentRestartTask(experimentTaskScheduleService, experimentInstanceService,
                    experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                    appId, taskScheduler, experimentTimerBiz, calculatorDispatcher, periodStartNoticer, periodEndNoticer, experimentSettingBiz, experimentSchemeBiz);
            experimentRestartTask.run();
            log.info("ApplicationRestarted succ.");
        } catch (Exception ex) {
            log.error("ApplicationRestarted fail.", ex);
        }
    }

    /**
     * 获取docker 和宿主机IP
     * @return
     */
/*    private String getIp(){
        DockerClient dockerClient = DefaultDockerClient.fromEnv().build();
        String containerId = "CONTAINER_ID";
        ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);
        String ipAddress = containerInfo.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        return ipAddress;
    }*/


   /* @SneakyThrows
    private String getIp() {
        Runtime rt = Runtime.getRuntime();

        //执行 ifconfig -a 命令，查询宿主机的ip配置
        String[] shell = {"/bin/bash", "-c", "sshpass -p 'abc@1234' ssh -o StrictHostKeyChecking=no -p 22 tmsceshi@10.73.100.6 ifconfig -a "};

        //执行hello.sh 脚本
        String[] shell1 = {"/bin/bash", "-c", "sshpass -p 'abc@1234' ssh -o StrictHostKeyChecking=no -p 22 tmsceshi@10.73.100.6 sh hello.sh "};
        Process exec = Runtime.getRuntime().exec(shell);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = null;
            String[] strArray = null;
            //逐一对每行内容进行操作
            while ((str = in.readLine()) != null) {
                System.out.println(str);
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            in.close();
        }
        return "";
    }*/
}
