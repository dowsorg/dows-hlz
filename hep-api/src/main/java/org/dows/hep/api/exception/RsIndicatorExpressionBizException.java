package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RsIndicatorExpressionBizException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RsIndicatorExpressionBizException(String msg) {
    super(msg);
  }

  public RsIndicatorExpressionBizException(Integer code, String msg) {
    super(code, msg);
  }

  public RsIndicatorExpressionBizException(Throwable throwable) {
    super(throwable);
  }

  public RsIndicatorExpressionBizException(StatusCode statusCode) {
    super(statusCode);
  }

  public RsIndicatorExpressionBizException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RsIndicatorExpressionBizException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RsIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RsIndicatorExpressionBizException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
