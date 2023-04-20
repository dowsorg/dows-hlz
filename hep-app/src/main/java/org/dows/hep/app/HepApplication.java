package org.dows.hep.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
* @description
*
* @author 
* @date 2023年4月14日 下午3:45:06
*/
@SpringBootApplication(scanBasePackages = {"org.dows.hep.*",
                                           "org.dows.framework.*",
                                           "org.dows.account.*",
                                           "org.dows.user.*",
                                           "org.dows.rbac.*"})
@MapperScan(basePackages = {"org.dows.*.mapper"})
public class HepApplication{
    public static void main(String[] args) {
        SpringApplication.run(HepApplication.class, args);
    }
}

