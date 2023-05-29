package org.dows.hep.api.base.question.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum QuestionTypeEnum {

    RADIO_SELECT("RADIO_SELECT", "单选"),
    MULTIPLE_SELECT("MULTIPLE_SELECT", "多选"),
    JUDGMENT("JUDGMENT", "判断"),
    SUBJECTIVE("SUBJECTIVE", "主观题"),
    MATERIAL("MATERIAL", "材料");

    private final String code;
    private final String name;

    // 是否是选择题
    public static boolean isSelect(String code) {
        if (code == null) {
            return false;
        }

        if (RADIO_SELECT.getCode().equals(code)) {
            return true;
        }
        if (MULTIPLE_SELECT.getCode().equals(code)) {
            return true;
        }
        return false;
    }

    public static QuestionTypeEnum getByCode(String code) {
        QuestionTypeEnum[] values = values();
        return Arrays.stream(values)
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(String code) {
        return getByCode(code).getName();
    }
}
