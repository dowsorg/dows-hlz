package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsIndicatorInstanceBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsIndicatorInstanceBizException(String msg) {
        super(msg);
    }

    public RsIndicatorInstanceBizException(Integer code, String msg) {
        super(code, msg);
    }

    public RsIndicatorInstanceBizException(Throwable throwable) {
        super(throwable);
    }

    public RsIndicatorInstanceBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsIndicatorInstanceBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsIndicatorInstanceBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsIndicatorInstanceBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsIndicatorInstanceBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
