package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author jx
 * @date 2023/5/5 10:02
 */
public class CaseIndicatorExpressionRefBizException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9061881000892474432L;

    public CaseIndicatorExpressionRefBizException(String msg) {
        super(msg);
    }

    public CaseIndicatorExpressionRefBizException(Integer code, String msg) {
        super(code, msg);
    }

    public CaseIndicatorExpressionRefBizException(Throwable throwable) {
        super(throwable);
    }

    public CaseIndicatorExpressionRefBizException(StatusCode statusCode) {
        super(statusCode);
    }

    public CaseIndicatorExpressionRefBizException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public CaseIndicatorExpressionRefBizException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public CaseIndicatorExpressionRefBizException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public CaseIndicatorExpressionRefBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
