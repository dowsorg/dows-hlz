//package org.dows.hep.biz.config;
//
//import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
//import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
//import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author runsix
// */
//@Configuration
//public class RsConfig {
//  @Value("${elasticjob.reg-center.server-lists:192.168.1.60:2181}")
//  private String serverLists;
//
//  @Value("${elasticjob.reg-center.namespace}")
//  private String namespace;
//
//  @Bean
//  public CoordinatorRegistryCenter createRegistryCenter() {
//    CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("192.168.1.60:2181", "dev-dows-hep"));
//    regCenter.init();
//    return regCenter;
//  }
//}
