package org.dows.hep.api.base.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private final static Map<String, QuestionTypeEnum> codeMap = new LinkedHashMap<>();

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
        QuestionTypeEnum questionTypeEnum = codeMap.get(code);
        if (questionTypeEnum == null) {
            Arrays.stream(QuestionTypeEnum.values())
                    .forEach(item -> codeMap.put(item.getCode(), item));
            questionTypeEnum = codeMap.get(code);
        }
        return questionTypeEnum;
    }

    public static String getNameByCode(String code) {
        return getByCode(code).getName();
    }
}
