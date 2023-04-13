package org.dows.hep.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* @description
*
* @author 
* @date 2023年4月13日 下午7:47:15
*/
@SpringBootApplication(scanBasePackages = {"org.dows.hep"})
public class HepApplication{
    public static void main(String[] args) {
        SpringApplication.run(HepApplication.class, args);
    }
}

