package org.dows.hep.biz.user.experiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumEventActionState;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.user.experiment.response.OrgNoticeResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.ExperimentEventBox;
import org.dows.hep.biz.vo.ExperimentEventJson;
import org.dows.hep.biz.vo.ExperimentOrgNoticeBox;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.service.ExperimentOrgNoticeService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/7 10:45
 */


@Service
@RequiredArgsConstructor
public class ExperimentOrgNoticeBiz {

    private final ExperimentOrgNoticeService experimentOrgNoticeService;
    private final IdGenerator idGenerator;

    //region save
    public boolean add(ExperimentOrgNoticeEntity row) {
        if(ShareUtil.XObject.isEmpty(row.getExperimentOrgNoticeId())){
            row.setExperimentOrgNoticeId(idGenerator.nextIdStr());
        }
        return experimentOrgNoticeService.save(row);
    }
    public boolean update(ExperimentOrgNoticeEntity row) {
        if(ShareUtil.XObject.notEmpty(row.getExperimentOrgNoticeId())) {
            return experimentOrgNoticeService.updateById(row);
        }else{
            return experimentOrgNoticeService.lambdaUpdate()
                    .eq(ExperimentOrgNoticeEntity::getExperimentOrgNoticeId, row.getExperimentOrgNoticeId())
                    .update(row);
        }
    }

    public boolean add(List<ExperimentOrgNoticeEntity> rows) {
        rows.forEach(i->{
            if(ShareUtil.XObject.isEmpty(i.getExperimentOrgNoticeId())){
                i.setExperimentOrgNoticeId(idGenerator.nextIdStr());
            }
        });
        return experimentOrgNoticeService.saveBatch(rows);

    }
    //endregion

    //region create
    //突发事件通知
    public ExperimentOrgNoticeEntity createNotice(ExperimentEventEntity src) throws JsonProcessingException {
        ExperimentEventBox eventBox = ExperimentEventBox.create(src);
        ExperimentEventJson eventData = eventBox.fromEventJsonOrDefault(false);
        ExperimentOrgNoticeEntity rst = new ExperimentOrgNoticeEntity()
                .setAppId(src.getAppId())
                .setExperimentInstanceId(src.getExperimentInstanceId())
                .setExperimentGroupId(src.getExperimentGroupId())
                .setExperimentOrgId(src.getExperimentOrgId())
                .setExperimentPersonId(src.getExperimentPersonId())
                .setPersonName(src.getPersonName())
                .setPeriods(src.getTriggeredPeriod())
                .setGameDay(src.getTriggerGameDay())
                .setNoticeTime(new Date())
                .setNoticeSrcType(EnumExperimentOrgNoticeType.EVENTTriggered.getCode())
                .setNoticeSrcId(src.getExperimentEventId())
                .setTitle(eventData.getCaseEventName())
                .setContent(eventData.getDescr())
                .setTips(eventData.getTips())
                .setReadState(0)
                .setActionState(EnumEventActionState.TODO.getCode());
        ExperimentOrgNoticeBox.create(rst)
                .setJsonData(createNoticeAction(eventBox))
                .toActionsJson(true);
        return rst;
    }

    public List<ExptOrgNoticeActionVO> createNoticeAction(ExperimentEventBox src) throws JsonProcessingException {
        ExperimentEventJson eventData = src.fromEventJsonOrDefault(false);
        return ShareUtil.XCollection.map(eventData.getActions(), true, i->
                CopyWrapper.create(ExptOrgNoticeActionVO::new).endFrom(i).setActedFlag(0)) ;
    }
    //endregion

    //region response
    public OrgNoticeResponse CreateOrgNoticeResponse(ExperimentOrgNoticeEntity notice) throws JsonProcessingException{
        return CreateOrgNoticeResponse(ExperimentOrgNoticeBox.create(notice));
    }
    public OrgNoticeResponse CreateOrgNoticeResponse(ExperimentOrgNoticeBox noticeBox) throws JsonProcessingException{
        if(ShareUtil.XObject.isEmpty(noticeBox)){
            return null;
        }
        OrgNoticeResponse rst= CopyWrapper.create(OrgNoticeResponse::new)
                .endFrom(noticeBox.getEntity());
        if(noticeBox.isEventNotice()) {
            rst.setActions(noticeBox.fromActionsJson(false));
        }
        return rst;
    }
    //endregion

}
