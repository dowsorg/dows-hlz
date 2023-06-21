package org.dows.hep.biz.util;

import cn.hutool.core.util.StrUtil;
import org.dows.framework.crud.api.CrudEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil {

    public static String distinctColumn(Class<? extends CrudEntity> claszz, String column) {

        List<String> collect = Arrays.stream(claszz.getDeclaredFields())
                .map(f -> f.getName()).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : collect) {
            String underlineCase = StrUtil.toUnderlineCase(s);
            if (s.equals(column)) {
                stringBuilder.append("DISTINCT ").append(underlineCase).append(",");
            } else {
                stringBuilder.append(" " + underlineCase + ",");
            }
        }

        //StringBuilder拆分为数组
        int index = -1;
        List<String> list = Arrays.asList(stringBuilder.toString().split(","));
        for(int i = 0; i< list.size(); i++){
            if(list.get(i).startsWith("DISTINCT")){
                index = i;
                break;
            }
        }
        Collections.swap(list,index,0);
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", list));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(EntityUtil.distinctColumn(ExperimentParticipatorEntity.class, "experimentInstanceId"));
    }

}
