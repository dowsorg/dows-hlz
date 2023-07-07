package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsIndicatorExpressionException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsIndicatorExpressionException(String msg) {
        super(msg);
    }

    public RsIndicatorExpressionException(Integer code, String msg) {
        super(code, msg);
    }

    public RsIndicatorExpressionException(Throwable throwable) {
        super(throwable);
    }

    public RsIndicatorExpressionException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsIndicatorExpressionException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsIndicatorExpressionException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsIndicatorExpressionException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsIndicatorExpressionException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
