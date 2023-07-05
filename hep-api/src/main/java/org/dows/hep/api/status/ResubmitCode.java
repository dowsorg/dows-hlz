package org.dows.hep.api.status;

import org.dows.framework.api.StatusCode;

public enum ResubmitCode implements StatusCode {

    RESUBMIT(20001, "请勿重复提交");

    private int code;
    private String descr;

    ResubmitCode(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDescr() {
        return descr;
    }
}
