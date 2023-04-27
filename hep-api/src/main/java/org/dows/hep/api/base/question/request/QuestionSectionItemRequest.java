package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author fhb
 * @description
 * @date 2023/4/24 19:37
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionSectionItemRequest 对象", title = "问题集 item Request")
public class QuestionSectionItemRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "是否必填")
    private Integer required;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题")
    private QuestionRequest questionRequest;



    // JsonIgnore
    @Schema(title = "应用ID")
    @JsonIgnore
    private String appId;

    @Schema(title = "问题集ID")
    @JsonIgnore
    private String questionSectionId;

    @Schema(title = "问题集名称")
    @JsonIgnore
    private String questionSectionName;

    @Schema(title = "问题ID")
    @JsonIgnore
    private String questionInstanceId;

    @Schema(title = "问题标题")
    @JsonIgnore
    private String questionTitle;

    @Schema(title = "问题描述")
    @JsonIgnore
    private String questionDescr;

    @Schema(title = "创建者账号Id")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者姓名")
    @JsonIgnore
    private String accountName;

    @Schema(title = "权限[000001]")
    @JsonIgnore
    private String permissions;

    @Schema(title = "问题集标识")
    @JsonIgnore
    private String questionSectionIdentifier;

    @Schema(title = "版本号")
    @JsonIgnore
    private String ver;

}
