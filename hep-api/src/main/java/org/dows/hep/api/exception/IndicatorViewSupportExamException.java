package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewSupportExamException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewSupportExamException(String msg) {
    super(msg);
  }

  public IndicatorViewSupportExamException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewSupportExamException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewSupportExamException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewSupportExamException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewSupportExamException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewSupportExamException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewSupportExamException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
