package org.dows.hep.biz.user.experiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumEventActionState;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.user.experiment.response.OrgNoticeResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;
import org.dows.hep.biz.dao.ExperimentParticipatorDao;
import org.dows.hep.biz.eval.ExperimentPersonCache;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.ExperimentEventBox;
import org.dows.hep.biz.vo.ExperimentEventJson;
import org.dows.hep.biz.vo.ExperimentOrgNoticeBox;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentOrgNoticeService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/7 10:45
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentOrgNoticeBiz {

    private final ExperimentOrgNoticeService experimentOrgNoticeService;

    private final AccountInstanceApi accountInstanceApi;
    private final IdGenerator idGenerator;

    //region save
    public boolean add(ExperimentOrgNoticeEntity row) {
        if(ShareUtil.XObject.isEmpty(row.getExperimentOrgNoticeId())){
            row.setExperimentOrgNoticeId(idGenerator.nextIdStr());
        }
        return experimentOrgNoticeService.save(row);
    }
    public boolean update(ExperimentOrgNoticeEntity row) {
        if(ShareUtil.XObject.isEmpty(row.getExperimentOrgNoticeId())) {
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
        return createNotice(src, new HashMap<>());
    }
    public ExperimentOrgNoticeEntity createNotice(ExperimentEventEntity src,Map<String,String> mapAvatar) throws JsonProcessingException {
        ExperimentEventBox eventBox = ExperimentEventBox.create(src);
        ExperimentEventJson eventData = eventBox.fromEventJsonOrDefault(false);
       ;

        ExperimentOrgNoticeEntity rst = new ExperimentOrgNoticeEntity()
                .setAppId(src.getAppId())
                .setExperimentInstanceId(src.getExperimentInstanceId())
                .setExperimentGroupId(src.getExperimentGroupId())
                .setExperimentPersonId(src.getExperimentPersonId())
                .setExperimentOrgId(Optional.ofNullable(ExperimentPersonCache.Instance().getPerson(src.getExperimentInstanceId(), src.getExperimentPersonId()))
                        .map(ExperimentPersonEntity::getExperimentOrgId)
                        .orElse(src.getExperimentOrgId()))
                .setAccountId(src.getAccountId())
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
        return fillAvatar(mapAvatar, rst);
    }

    public List<ExptOrgNoticeActionVO> createNoticeAction(ExperimentEventBox src) throws JsonProcessingException {
        ExperimentEventJson eventData = src.fromEventJsonOrDefault(false);
        return ShareUtil.XCollection.map(eventData.getActions(), true, i->
                CopyWrapper.create(ExptOrgNoticeActionVO::new).endFrom(i).setActedFlag(0)) ;
    }
    //endregion

    //region 随访计划通知

    /*public ExperimentOrgNoticeEntity createNotice(ExperimentFollowupPlanEntity src, Map<String,String> mapAvatar) throws JsonProcessingException {

        ExperimentOrgNoticeEntity rst = new ExperimentOrgNoticeEntity()
                .setAppId(src.getAppId())
                .setExperimentInstanceId(src.getExperimentInstanceId())
                .setExperimentGroupId(src.getExperimentGroupId())
                .setExperimentOrgId(src.getExperimentOrgId())
                .setExperimentPersonId(src.getExperimentPersonId())
                .setAccountId(src.getAccountId())
                .setPersonName(src.getPersonName())
                .setPeriods(src.getTriggeredPeriod())
                .setGameDay(src.getTriggerGameDay())
                .setNoticeTime(new Date())
                .setNoticeSrcType(EnumExperimentOrgNoticeType.FOLLOWUP.getCode())
                .setNoticeSrcId(src.getExperimentEventId())
                .setTitle(eventData.getCaseEventName())
                .setContent(eventData.getDescr())
                .setTips(eventData.getTips())
                .setReadState(0)
                .setActionState(EnumEventActionState.TODO.getCode());
        ExperimentOrgNoticeBox.create(rst)
                .setJsonData(createNoticeAction(eventBox))
                .toActionsJson(true);
        return fillAvatar(mapAvatar, rst);
    }*/
    //endregion

    /**
     * 填充人物头像
     * @param src
     * @return
     */
    public ExperimentOrgNoticeEntity fillAvatar(Map<String,String> mapAvatar, ExperimentOrgNoticeEntity src){
        try {
            final String accountId=src.getAccountId();
            if (ShareUtil.XObject.isEmpty(accountId)) {
                return src;
            }
            String avatar=mapAvatar.get(accountId);
            if(ShareUtil.XObject.isEmpty(avatar)){
                avatar= Optional.ofNullable(accountInstanceApi.getAccountInstanceByAccountId(src.getAccountId()))
                        .map(AccountInstanceResponse::getAvatar)
                        .orElse(null);
                if(ShareUtil.XObject.notEmpty(avatar)){
                    mapAvatar.put(accountId,avatar);
                }
            }
            src.setAvatar(avatar);
        }catch (Exception ex){
            log.error(String.format("ExperimentOrgNoticeBiz.fillAvatar accountId:%s",src.getAccountId()),ex);
        }
        return src;
    }

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

    //region websocket
    public  Map<String,List<OrgNoticeResponse>> getWebSocketNotice(String experimentInstanceId, List<ExperimentOrgNoticeEntity> src)  throws JsonProcessingException{
        Set<String> groupIds=ShareUtil.XCollection.toSet(src, ExperimentOrgNoticeEntity::getExperimentGroupId);
        List<ExperimentParticipatorEntity> rowsParticipator= CrudContextHolder.getBean(ExperimentParticipatorDao.class).getAccountIdsByGroupId(experimentInstanceId, groupIds,
                ExperimentParticipatorEntity::getAccountId,
                ExperimentParticipatorEntity::getExperimentGroupId,
                ExperimentParticipatorEntity::getExperimentOrgIds);
        Map<String,List<OrgNoticeResponse>> rst=new HashMap<>();
        List<OrgNoticeResponse> notices=null;
        String[] orgIds;
        for(ExperimentParticipatorEntity rowAccount:rowsParticipator){
            if(null==notices||notices.size()>0) {
                notices = new ArrayList<>();
            }
            orgIds=null;
            if(ShareUtil.XObject.notEmpty(rowAccount.getExperimentOrgIds())){
                orgIds=rowAccount.getExperimentOrgIds().split(",");
            }
            for(ExperimentOrgNoticeEntity rowNotice:src){
                if(!ShareUtil.XObject.nullSafeEquals(rowNotice.getExperimentGroupId(), rowAccount.getExperimentGroupId()) ){
                    continue;
                }
                if(null!=orgIds&&!ShareUtil.XArray.contains(orgIds, rowNotice.getExperimentOrgId())){
                    continue;
                }
                notices.add(CreateOrgNoticeResponse(rowNotice));
            }
            if(notices.size()>0){
                rst.put(rowAccount.getAccountId(), notices);
            }
        }
        return rst;
    }
    //endregion

}
