package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/8 10:19
 */
public class ExperimentParticipatorException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public ExperimentParticipatorException(String msg) {
        super(msg);
    }

    public ExperimentParticipatorException(Integer code, String msg) {
        super(code, msg);
    }

    public ExperimentParticipatorException(Throwable throwable) {
        super(throwable);
    }

    public ExperimentParticipatorException(StatusCode statusCode) {
        super(statusCode);
    }

    public ExperimentParticipatorException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public ExperimentParticipatorException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public ExperimentParticipatorException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public ExperimentParticipatorException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
