package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

public class ExperimentException extends BaseException {
    public ExperimentException(String msg) {
        super(msg);
    }

    public ExperimentException(Integer code, String msg) {
        super(code, msg);
    }

    public ExperimentException(Throwable throwable) {
        super(throwable);
    }

    public ExperimentException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ExperimentException(StatusCode statusCode) {
        super(statusCode);
    }

    public ExperimentException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public ExperimentException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public ExperimentException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public ExperimentException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
