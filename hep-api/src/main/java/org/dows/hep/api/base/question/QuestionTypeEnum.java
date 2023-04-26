package org.dows.hep.api.base.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
