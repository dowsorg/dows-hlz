package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

/**
 * @author jx
 * @date 2023/6/26 14:39
 */
public class TagsException extends BaseException {
    public TagsException(String msg) {
        super(msg);
    }

    public TagsException(Integer code, String msg) {
        super(code, msg);
    }

    public TagsException(Throwable throwable) {
        super(throwable);
    }

    public TagsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TagsException(StatusCode statusCode) {
        super(statusCode);
    }

    public TagsException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public TagsException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public TagsException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public TagsException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
