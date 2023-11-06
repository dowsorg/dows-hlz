package org.dows.hep.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/11/6 16:46
 */

@Component
@ConfigurationProperties(prefix = "score-settings")
@Data
public class ScoreSettingsProperties {

    @JsonProperty("hp-score-min")
    private String hpScoreMin;

    @JsonProperty("hp-score-max")
    private String hpScoreMax;

    @JsonProperty("money-score-min")
    private String moneyScoreMin;

    @JsonProperty("money-score-max")
    private String moneyScoreMax;

    @JsonProperty("judge-score-min")
    private String judgeScoreMin;

    @JsonProperty("judge-score-max")
    private String judgeScoreMax;
}
