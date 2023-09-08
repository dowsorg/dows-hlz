package org.dows.hep.config.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* @description
*
* @author 
* @date 2023年4月14日 下午3:45:06
*/
@SpringBootApplication(scanBasePackages = {"org.dows.hep.config.mongo","org.dows.framework.*"})
@Slf4j
public class HepApplication {
    public static void main(String[] args) {
        //TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        System.setProperty("SERVICE_NAME","hep");
        SpringApplication.run(HepApplication.class, args);
    }

}

