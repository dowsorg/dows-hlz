package org.dows.hep.biz.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;
import java.io.Serial;

/**
 * @author jx
 * @date 2023/4/24 17:22
 */
public class MaterialException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public MaterialException(String msg) {
        super(msg);
    }

    public MaterialException(Integer code, String msg) {
        super(code, msg);
    }

    public MaterialException(Throwable throwable) {
        super(throwable);
    }

    public MaterialException(StatusCode statusCode) {
        super(statusCode);
    }

    public MaterialException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public MaterialException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public MaterialException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public MaterialException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
