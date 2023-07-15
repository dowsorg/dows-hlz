package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.CaseIndicatorInstanceEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:37
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapCaseIndicatorInstanceEntity", title = "案例库指标快照")
@TableName("snap_case_indicator_instance")
public class SnapCaseIndicatorInstanceEntity extends CaseIndicatorInstanceEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;
}
