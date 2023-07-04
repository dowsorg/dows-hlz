//package org.dows.hep.event.handler;
//
//import cn.hutool.core.bean.BeanUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.hep.api.event.source.ExptQuestionnaireAllotEventSource;
//import org.dows.hep.api.user.experiment.request.ExptQuestionnaireAllotRequest;
//import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
//import org.dows.hep.entity.ExperimentParticipatorEntity;
//import org.dows.hep.service.ExperimentParticipatorService;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author fhb
// * @version 1.0
// * @description `沙盘`为参与者分配机构的时候，`publish` 该事件以完成 `知识答题` 的分配
// * @date 2023/6/19 23:18
// **/
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class ExptQuestionnaireAllotHandler extends AbstractEventHandler implements EventHandler<ExptQuestionnaireAllotEventSource> {
//    private final ExperimentParticipatorService experimentParticipatorService;
//    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
//
//    @Override
//    public void exec(ExptQuestionnaireAllotEventSource source) {
//        if (BeanUtil.isEmpty(source)) {
//            return;
//        }
//        String experimentInstanceId = source.getExperimentInstanceId();
//        String experimentGroupId = source.getExperimentGroupId();
//        List<ExperimentParticipatorEntity> list = experimentParticipatorService.lambdaQuery()
//                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
//                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
//                .list();
//
//        ArrayList<ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire> allotList = new ArrayList<>();
//        ExptQuestionnaireAllotRequest request = ExptQuestionnaireAllotRequest.builder()
//                .experimentInstanceId(experimentInstanceId)
//                .experimentGroupId(experimentGroupId)
//                .build();
//        list.forEach(ep -> {
//            String experimentOrgIds = ep.getExperimentOrgIds();
//            String accountId = ep.getAccountId();
//            String[] orgIds = experimentOrgIds.split(",");
//
//            ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire participatorWithQuestionnaire = new ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire();
//            participatorWithQuestionnaire.setAccountId(accountId);
//            participatorWithQuestionnaire.setExperimentOrgIds(List.of(orgIds));
//            allotList.add(participatorWithQuestionnaire);
//        });
//        request.setAllotList(allotList);
//
//        experimentQuestionnaireBiz.allotQuestionnaireMembers(request);
//    }
//}
