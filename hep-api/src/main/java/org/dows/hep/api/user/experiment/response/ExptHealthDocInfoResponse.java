package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.ExptIndicatorValLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 健康档案信息
 * @author : wuzl
 * @date : 2023/9/6 9:44
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "ExptHealthDocInfoResponse 对象", title = "健康档案信息")

public class ExptHealthDocInfoResponse {

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "游戏内总天数")
    private Long totalDays;

    @Schema(title = "当前健康指数")
    private String healthIndex;

    @Schema(title = "医疗占比")
    private String moneyScore;

    @Schema(title = "危险因素列表")
    private Set<String> risks;

    @Schema(title = "右下健康指数折线图")
    private ExptIndicatorValLine healthIndexLine;

    @Schema(title = "右上能量折线图(两个指标)")
    private final List<ExptIndicatorValLine> categEnergy=new ArrayList<>();

    @Schema(title = "左下指标折线图（6个指标）")
    private final List<ExptIndicatorValLine> categOther=new ArrayList<>();

}
