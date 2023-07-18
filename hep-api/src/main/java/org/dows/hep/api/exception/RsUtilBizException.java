package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsUtilBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsUtilBizException(String msg) {
        super(msg);
    }

    public RsUtilBizException(Integer code, String msg) {
        super(code, msg);
    }

    public RsUtilBizException(Throwable throwable) {
        super(throwable);
    }

    public RsUtilBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsUtilBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsUtilBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsUtilBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsUtilBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
