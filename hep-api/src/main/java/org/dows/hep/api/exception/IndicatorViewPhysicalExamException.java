package org.dows.hep.api.exception;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.io.Serial;

/**
 * @author runsix
 */
public class IndicatorViewPhysicalExamException extends BaseException {
  @Serial
  private static final long serialVersionUID = 9061881000892474432L;

  public IndicatorViewPhysicalExamException(String msg) {
    super(msg);
  }

  public IndicatorViewPhysicalExamException(Integer code, String msg) {
    super(code, msg);
  }

  public IndicatorViewPhysicalExamException(Throwable throwable) {
    super(throwable);
  }

  public IndicatorViewPhysicalExamException(StatusCode statusCode) {
    super(statusCode);
  }

  public IndicatorViewPhysicalExamException(StatusCode statusCode, Exception exception) {
    super(statusCode, exception);
  }

  public IndicatorViewPhysicalExamException(StatusCode statusCode, String msg) {
    super(statusCode, msg);
  }

  public IndicatorViewPhysicalExamException(StatusCode statusCode, Object[] args, String message) {
    super(statusCode, args, message);
  }

  public IndicatorViewPhysicalExamException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
    super(statusCode, args, message, cause);
  }
}
