package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class RsExperimentIndicatorExpressionBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public RsExperimentIndicatorExpressionBizException(String msg) {
        super(msg);
    }

    public RsExperimentIndicatorExpressionBizException(Integer code, String msg) {
        super(code, msg);
    }

    public RsExperimentIndicatorExpressionBizException(Throwable throwable) {
        super(throwable);
    }

    public RsExperimentIndicatorExpressionBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public RsExperimentIndicatorExpressionBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RsExperimentIndicatorExpressionBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RsExperimentIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RsExperimentIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
