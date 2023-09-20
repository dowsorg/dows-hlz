package org.dows.edw.domain;

import lombok.Data;
import org.dows.edw.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康问题|疾病[判断]
 * @TableName hep_health_problem
 */
@Data
@MongoEntity
public class HepHealthProblem implements Serializable, HepOperateEntity {
    /**
     * 健康问题ID
     */
    @FieldFill
    @MongoEntityId
    private Long hepHealthProblemId;

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
     * 诊断时间(仿真时间)
     */
    private LocalDateTime atDate;

    /**
     * 时间戳
     */
    @FieldFill
    private LocalDateTime dt;

    private static final long serialVersionUID = 1L;
}