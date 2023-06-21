package org.dows.hep.biz.util;

import cn.hutool.core.util.StrUtil;
import org.dows.framework.crud.api.CrudEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil {


    public static String getColumns(Class<? extends CrudEntity> claszz) {

        List<String> collect = Arrays.stream(claszz.getDeclaredFields())
                .map(f -> f.getName()).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : collect) {
            String underlineCase = StrUtil.toUnderlineCase(s);
            stringBuilder.append(underlineCase + ",");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();

    }


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
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();

    }

    public static void main(String[] args) {
        System.out.println(EntityUtil.distinctColumn(ExperimentParticipatorEntity.class, "experimentInstanceId"));
    }

}
