package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/7/7 14:48
 */
@Data
@Accessors(chain = true)
public class ExperimentOrgNoticeBox  {

    private ExperimentOrgNoticeBox(ExperimentOrgNoticeEntity src){
        this.entity=src;
    }
    public static ExperimentOrgNoticeBox create(ExperimentOrgNoticeEntity src){
        return new ExperimentOrgNoticeBox(src);
    }

    @Schema(title = "通知实体")
    private final ExperimentOrgNoticeEntity entity;
    @JsonIgnore
    @Schema(title = "事件处理措施列表")
    private List<ExptOrgNoticeActionVO> jsonData;

    public boolean isEventNotice(){
        return EnumExperimentOrgNoticeType.EVENTTriggered.getCode().equals(entity.getNoticeSrcType());
    }
    public boolean isFollowUp(){
        return EnumExperimentOrgNoticeType.FOLLOWUP.getCode().equals(entity.getNoticeSrcType());
    }


    public String toActionsJsonOrDefault(boolean forceFlag) throws JsonProcessingException{
        return Optional.ofNullable(toActionsJson(forceFlag)).orElse("");
    }
    public String toActionsJson(boolean forceFlag) throws JsonProcessingException {
        if(!forceFlag&& ShareUtil.XObject.notEmpty(entity.getEventActions() )){
            return entity.getEventActions();
        }
        String json= JacksonUtil.toJson(jsonData,true);
        entity.setEventActions(json);
        return json;
    }
    public List<ExptOrgNoticeActionVO> fromActionsJsonOrDefault(boolean forceFlag) throws JsonProcessingException {
        return Optional.ofNullable(fromActionsJson(forceFlag)).orElse(new ArrayList<>());
    }
    public List<ExptOrgNoticeActionVO> fromActionsJson(boolean forceFlag) throws JsonProcessingException {
        if (!forceFlag && ShareUtil.XObject.notEmpty(this.jsonData)) {
            return this.jsonData;
        }
        return jsonData = JacksonUtil.fromJson(entity.getEventActions(), new TypeReference<>() {
        });
    }
}
