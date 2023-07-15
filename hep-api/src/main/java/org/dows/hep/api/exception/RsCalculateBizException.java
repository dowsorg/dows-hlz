package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsCalculateBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsCalculateBizException(String msg) {
        super(msg);
    }

    public RsCalculateBizException(Integer code, String msg) {
        super(code, msg);
    }

    public RsCalculateBizException(Throwable throwable) {
        super(throwable);
    }

    public RsCalculateBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsCalculateBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsCalculateBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsCalculateBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsCalculateBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
