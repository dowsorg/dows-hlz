package org.dows.hep.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description mongo 属性
 * @date 2023/9/14 11:45
 **/
@Component
@ConfigurationProperties(prefix = "mongo")
@Data
public class MongoProperties {

    private Boolean enable;
}
