package org.dows.hep.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.dows.hep.biz.job.RsJob;
import org.springframework.context.annotation.Configuration;

/**
 * @author runsix
 */
@Configuration
@RequiredArgsConstructor
public class RsJobConfig {
  private final CoordinatorRegistryCenter coordinatorRegistryCenter;

  @PostConstruct
  public void job() {
    new ScheduleJobBootstrap(coordinatorRegistryCenter, new RsJob(), JobConfiguration.newBuilder("RsJob", 3).cron("0/30 * * * * ?").build()).schedule();
  }
}
