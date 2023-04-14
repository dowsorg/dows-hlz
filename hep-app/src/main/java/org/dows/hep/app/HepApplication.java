package org.dows.hep.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* @description
*
* @author 
* @date 2023年4月14日 上午10:20:00
*/
@SpringBootApplication(scanBasePackages = {"org.dows.hep"})
public class HepApplication{
    public static void main(String[] args) {
        SpringApplication.run(HepApplication.class, args);
    }
}

