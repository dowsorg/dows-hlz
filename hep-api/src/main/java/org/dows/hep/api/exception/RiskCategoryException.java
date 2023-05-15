package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class RiskCategoryException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public RiskCategoryException(String msg) {
    super(msg);
  }

  public RiskCategoryException(Integer code, String msg) {
    super(code, msg);
  }

  public RiskCategoryException(Throwable throwable) {
    super(throwable);
  }

  public RiskCategoryException(StatusCode statusCode) {
    super(statusCode);
  }

  public RiskCategoryException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public RiskCategoryException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public RiskCategoryException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public RiskCategoryException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
