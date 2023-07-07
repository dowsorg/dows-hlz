package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutConditionRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
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
      log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionSource 的指标公式来源:{} 不合法", source);
      throw new RsIndicatorExpressionException("检查指标公式条件有误，指标公式来源不合法");
    }
  }

  private static void checkConditionNameAndVal(String conditionNameList, String conditionValList) {
    if (StringUtils.isBlank(conditionNameList)) {
      if (StringUtils.isBlank(conditionValList)) {
        /* runsix:right, no condition */
      } else {
        /* runsix:conditionNameList is blank but conditionValList is not blank */
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList is blank but conditionValList is not blank");
        throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表为空，但是条件值列表不为空");
      }
    } else {
      if (StringUtils.isBlank(conditionValList)) {
        /* runsix:conditionNameList is not blank but conditionValList is blank */
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList is not blank but conditionValList is blank");
        throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表为空，但是条件值列表不为空");
      } else {
        String[] conditionNameArray = conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        String[] conditionValArray = conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        if (conditionNameArray.length != conditionValArray.length) {
          log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList size:{}, conditionValList size:{}, is not same", conditionNameArray.length, conditionValArray.length);
          throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表与条件值列表不一致");
        }
      }
    }
  }

  private static void checkConditionMustBeBoolean(Integer field, ) {

  }

  public static boolean checkCondition(RsIndicatorExpressionCheckoutConditionRequest rsIndicatorExpressionCheckoutConditionRequest) {
    boolean checkConditionResult = true;
    Integer source = rsIndicatorExpressionCheckoutConditionRequest.getSource();
    Integer field = rsIndicatorExpressionCheckoutConditionRequest.getField();
    String conditionNameList = rsIndicatorExpressionCheckoutConditionRequest.getConditionNameList();
    String conditionValList = rsIndicatorExpressionCheckoutConditionRequest.getConditionValList();
    checkConditionSource(source);
    checkConditionNameAndVal(conditionNameList, conditionValList);

    return checkConditionResult;
  }

}
