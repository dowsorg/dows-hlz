package org.dows.hep.api.base.question;

public enum QuestionTypeEnum {

    RADIO_SELECT("RADIO_SELECT", "单选题"),
    MULTIPLE_SELECT("MULTIPLE_SELECT", "多选题"),
    JUDGMENT("JUDGMENT", "判断题"),
    SUBJECTIVE("SUBJECTIVE", "主观题"),
    MATERIAL("MATERIAL", "材料题");

    private final String code;
    private final String name;

    QuestionTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

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
