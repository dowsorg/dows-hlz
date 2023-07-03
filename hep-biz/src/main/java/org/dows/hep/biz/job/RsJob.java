package org.dows.hep.biz.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author runsix
 */
@Component
public class RsJob implements SimpleJob {
  @Override
  public void execute(ShardingContext context) {
    System.out.println("RsJob-time:" + new Date());
  }
}
