package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsCopyException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsCopyException(String msg) {
        super(msg);
    }

    public RsCopyException(Integer code, String msg) {
        super(code, msg);
    }

    public RsCopyException(Throwable throwable) {
        super(throwable);
    }

    public RsCopyException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsCopyException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsCopyException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsCopyException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsCopyException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
