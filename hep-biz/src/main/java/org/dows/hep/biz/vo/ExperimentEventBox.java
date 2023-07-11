package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/7/7 14:48
 */
@Data
@Accessors(chain = true)
public class ExperimentEventBox  {

    private ExperimentEventBox(ExperimentEventEntity src){
        this.entity=src;
    }
    public static ExperimentEventBox create(ExperimentEventEntity src){
        return new ExperimentEventBox(src);
    }
    @Schema(title = "事件实体")
    private final ExperimentEventEntity entity;
    @JsonIgnore
    @Schema(title = "事件内容")
    private ExperimentEventJson eventJsonData;

    @JsonIgnore
    @Schema(title = "事件措施")
    private List<ExptOrgNoticeActionVO> actionJsonData;


    //region eventJson
    public String toEventJsonOrDefault(boolean forceFlag) throws JsonProcessingException{
        return Optional.ofNullable(toEventJson(forceFlag)).orElse("");
    }
    public String toEventJson(boolean forceFlag) throws JsonProcessingException {
        if(!forceFlag&& ShareUtil.XObject.notEmpty(entity.getEventJson() )){
            return entity.getEventJson();
        }
        String json= JacksonUtil.toJson(eventJsonData,true);
        entity.setEventJson(json);
        return json;
    }

    public ExperimentEventJson fromEventJsonOrDefault(boolean forceFlag) throws JsonProcessingException{
        return Optional.ofNullable(fromEventJson(forceFlag)).orElse(new ExperimentEventJson());
    }
    public ExperimentEventJson fromEventJson(boolean forceFlag) throws JsonProcessingException {
        if (!forceFlag && ShareUtil.XObject.notEmpty(this.eventJsonData)) {
            return  this.eventJsonData;
        }
        return eventJsonData = JacksonUtil.fromJson(entity.getEventJson(), ExperimentEventJson.class);
    }
    //endregion

    //actionJson
    public String toActionJsonOrDefault(boolean forceFlag) throws JsonProcessingException{
        return Optional.ofNullable(toActionJson(forceFlag)).orElse("");
    }
    public String toActionJson(boolean forceFlag) throws JsonProcessingException {
        if(!forceFlag&& ShareUtil.XObject.notEmpty(entity.getActionJson() )){
            return entity.getActionJson();
        }
        String json= JacksonUtil.toJson(actionJsonData,true);
        entity.setActionJson(json);
        return json;
    }

    public List<ExptOrgNoticeActionVO> fromActionJsonOrDefault(boolean forceFlag) throws JsonProcessingException{
        return Optional.ofNullable(fromActionJson(forceFlag)).orElse(new ArrayList<>());
    }
    public List<ExptOrgNoticeActionVO> fromActionJson(boolean forceFlag) throws JsonProcessingException {
        if (!forceFlag && ShareUtil.XObject.notEmpty(this.actionJsonData)) {
            return  this.actionJsonData;
        }
        return actionJsonData = JacksonUtil.fromJson(entity.getActionJson(), new TypeReference<>() {
        });
    }
    //endregion



}
