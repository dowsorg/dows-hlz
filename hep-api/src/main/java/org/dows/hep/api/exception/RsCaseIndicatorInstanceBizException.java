package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsCaseIndicatorInstanceBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsCaseIndicatorInstanceBizException(String msg) {
        super(msg);
    }

    public RsCaseIndicatorInstanceBizException(Integer code, String msg) {
        super(code, msg);
    }

    public RsCaseIndicatorInstanceBizException(Throwable throwable) {
        super(throwable);
    }

    public RsCaseIndicatorInstanceBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsCaseIndicatorInstanceBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsCaseIndicatorInstanceBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsCaseIndicatorInstanceBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsCaseIndicatorInstanceBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
