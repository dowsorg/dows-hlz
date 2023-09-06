package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:15
 */
@Data
@Accessors(chain = true)
public class EvalPersonOnceData {

    @Schema(title = "计算批次")
    private Integer evalNo;

    @Schema(title = "同步状态")
    private EnumEvalSyncState syncState;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "计算天数")
    private Integer evalDay;

    @Schema(title = "计算时间")
    private Date evalTime;

    @Schema(title = "上次计算天数")
    private Integer lastEvalDay;

    @Schema(title = "当前健康指数")
    private String healthIndex;

    @Schema(title = "上次健康指数")
    private String lastHealthIndex;

    @Schema(title = "本次资金")
    private String money;

    @Schema(title = "上次资金")
    private String lastMoney;
    @Schema(title = "医疗占比")
    private String moneyScore;

    @Schema(title = "危险因素列表")
    private List<EvalRiskValues> risks;

    @Schema(title = "指标列表")
    private ConcurrentMap<String,EvalIndicatorValues> mapIndicators;


}
