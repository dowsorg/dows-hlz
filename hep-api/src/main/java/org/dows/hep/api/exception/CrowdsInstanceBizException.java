package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class CrowdsInstanceBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public CrowdsInstanceBizException(String msg) {
        super(msg);
    }

    public CrowdsInstanceBizException(Integer code, String msg) {
        super(code, msg);
    }

    public CrowdsInstanceBizException(Throwable throwable) {
        super(throwable);
    }

    public CrowdsInstanceBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public CrowdsInstanceBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public CrowdsInstanceBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public CrowdsInstanceBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public CrowdsInstanceBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
