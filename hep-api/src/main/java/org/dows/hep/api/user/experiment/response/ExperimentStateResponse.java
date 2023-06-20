package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.hep.api.enums.ExperimentStateEnum;

import java.util.Date;

@Data
public class ExperimentStateResponse {

    private ExperimentStateEnum experimentStateEnum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "开始时间")
    private Date experimentStartTime;
    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "倒计时时间")
    private Long countDownTime;

    public Long getCountDownTime() {
        countDownTime = experimentStartTime.getTime() - System.currentTimeMillis();
        return countDownTime;
    }
}
