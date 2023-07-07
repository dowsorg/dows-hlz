package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutConditionRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsIndicatorExpressionBiz {
  private static void checkConditionSource(Integer source) {
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = EnumIndicatorExpressionSource.getBySource(source);
    if (Objects.isNull(enumIndicatorExpressionSource)) {
      log.error("RsIndicatorExpressionBiz.checkCondition 的指标公式来源:{} 不合法", source);
      throw new RsIndicatorExpressionException("检查指标公式条件有误，指标公式来源不合法");
    }
  }

  public static boolean checkCondition(RsIndicatorExpressionCheckoutConditionRequest rsIndicatorExpressionCheckoutConditionRequest) {
    boolean checkConditionResult = true;
    Integer source = rsIndicatorExpressionCheckoutConditionRequest.getSource();
    checkConditionSource(source);
    return checkConditionResult;
  }

}
