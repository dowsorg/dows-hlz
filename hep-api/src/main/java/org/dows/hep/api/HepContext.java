package org.dows.hep.api;

import lombok.Data;
import org.dows.hep.api.enums.ExperimentStateEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class HepContext {
    private static ThreadLocal<HepContext> ContextThreadLocal = new ThreadLocal<>();
    private static Map<String, HepContext> ExperimentContextMap = new ConcurrentHashMap<>();

    private String appId;
    private String experimentId;
    private String experimentName;
    private Integer periods;
    // 实验状态
    private ExperimentStateEnum state;
    private List<ExperimentGroup> experimentGroups;

    private Integer groupCount;

    public static void set(HepContext hepContext) {
        //ContextThreadLocal.set(experimentContext);
        ExperimentContextMap.put(hepContext.getExperimentId(), hepContext);
    }

    public static HepContext get() {
        HepContext hepContext = ContextThreadLocal.get();
        return hepContext;
    }

    public static List<HepContext> getMap() {
        List<HepContext> contextList = new ArrayList<>();
        contextList.addAll(ExperimentContextMap.values());
        return contextList;
    }

    public static void remove() {
        ContextThreadLocal.remove();
    }


    public static HepContext getExperimentContext(String experimentId) {
       /* HepContext hepContext = ExperimentContextMap.get(experimentId);
        if(hepContext == null){
            hepContext = new HepContext();
        }
        return hepContext;*/
        return ExperimentContextMap.get(experimentId);
    }

    @Data
    public static class ExperimentGroup {
        private String groupId;
        private String groupName;
        private String groupAlias;
        private String memberCount;
        private List<ExperimentParticipator> experimentTeachers;
        private List<ExperimentParticipator> experimentStudents;
    }

    @Data
    public static class ExperimentParticipator {
        private String accountId;
        private String accountName;
        private String className;
        private String orgId;
        private Integer type;
    }

}
