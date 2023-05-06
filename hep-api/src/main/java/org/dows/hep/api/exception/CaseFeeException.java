package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class CaseFeeException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public CaseFeeException(String msg) {
        super(msg);
    }

    public CaseFeeException(Integer code, String msg) {
        super(code, msg);
    }

    public CaseFeeException(Throwable throwable) {
        super(throwable);
    }

    public CaseFeeException(StatusCode statusCode) {
        super(statusCode);
    }

    public CaseFeeException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public CaseFeeException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public CaseFeeException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public CaseFeeException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
