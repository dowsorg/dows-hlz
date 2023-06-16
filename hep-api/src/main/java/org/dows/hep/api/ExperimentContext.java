package org.dows.hep.api;

import lombok.Data;
import org.dows.hep.api.enums.ExperimentStateEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ExperimentContext {
    private static ThreadLocal<ExperimentContext> ContextThreadLocal = new ThreadLocal<>();
    private static Map<String, ExperimentContext> ExperimentContextMap = new ConcurrentHashMap<>();


    private String experimentId;
    private String experimentName;
    // 实验状态
    private ExperimentStateEnum state;
    private List<ExperimentGroup> experimentGroups;

    public static void set(ExperimentContext experimentContext) {
        //ContextThreadLocal.set(experimentContext);
        ExperimentContextMap.put(experimentContext.getExperimentId(), experimentContext);
    }

    public static ExperimentContext get() {
        ExperimentContext experimentContext = ContextThreadLocal.get();
        return experimentContext;
    }

    public static List<ExperimentContext> getMap() {
        List<ExperimentContext> contextList = new ArrayList<>();
        contextList.addAll(ExperimentContextMap.values());
        return contextList;
    }

    public static void remove() {
        ContextThreadLocal.remove();
    }


    public static ExperimentContext getExperimentContext(String experimentId) {
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
