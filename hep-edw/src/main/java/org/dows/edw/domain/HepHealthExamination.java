package org.dows.edw.domain;

import lombok.Data;
import org.dows.edw.FieldFill;
import org.dows.edw.LogicDel;
import org.dows.edw.MongoEntity;
import org.dows.edw.MongoEntityId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康检查[查看类]
 * @TableName hep_health_examination
 */
@Data
@MongoEntity
public class HepHealthExamination implements Serializable {
    /**
     * 体格检查Id
     */
    @FieldFill
    @MongoEntityId
    private Long hepHealthExaminationId;

    /**
     * 实验ID
     */
    private Long experimentInstanceId;

    /**
     * 实验小组ID
     */
    private Long experimentGroupId;

    /**
     * 操作者ID[小组成员]
     */
    private Long operatorId;

    /**
     * 实验人物ID[患者|居委主任|消防员]
     */
    private Long personId;

    /**
     * 机构树ID
     */
    private Long orgTreeId;

    /**
     * 流程Id[挂号ID|就诊ID]
     */
    private String flowId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * (职责|能力|功能|菜单)名称[体格检查报告,辅助检查报告......]
     */
    private String functionName;

    /**
     * (职责|能力|功能|菜单|指标)code
     */
    private String functionCode;

    /**
     * 记录值json数组
     */
    private String data;

    /**
     * 辅助检查结果解析
     */
    private String resultExplain;

    /**
     * 期数
     */
    private Integer period;

    /**
     * 所在天数
     */
    private Integer onDay;

    /**
     * 序号
     */
    private Integer seq;

    /**
     * 数据版本号
     */
    private Integer ver;

    /**
     * 逻辑删除
     */
    @FieldFill
    @LogicDel
    private Integer deleted;

    /**
     * 检查时间(仿真时间)
     */
    private LocalDateTime onDate;

    /**
     * 时间戳
     */
    @FieldFill
    private LocalDateTime dt;

    private static final long serialVersionUID = 1L;
}