package org.dows.hep.biz.util;

import org.dows.hep.api.user.experiment.response.EchartsDataResonse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/7/13 9:57
 */
public class EchartsUtils {
    /**
     * 开闭区间正则表达式
     */
    private static final Pattern NUM_RANGE_PATTERN = Pattern.compile("[\\[|\\(]\\s?\\d+\\s?,\\s?\\d+\\s?[\\)|\\]]");

    /**
     * 左半区间正则表达式
     */
    private static final Pattern LEFT_NUM_RANGE_PATTERN = Pattern.compile("[\\[|\\(]\\s?\\d+\\s?,\\s?[\\)|\\]]");

    /**
     * 右半区间正则表达式
     */
    private static final Pattern RIGHT_NUM_RANGE_PATTERN = Pattern.compile("[\\[|\\(],\\s?\\d+\\s?[\\)|\\]]");

    //数据为空赋值0
    public static List<EchartsDataResonse> fillEmptydata(List<EchartsDataResonse> statList, List<String> dataList) {
        if (statList != null && statList.size() > 0) {
            List<String> nameList = new ArrayList<>();
            statList.forEach(s -> {
                nameList.add(s.getName());
            });
            dataList.forEach(g -> {
                if (!nameList.contains(g)) {
                    EchartsDataResonse stat = new EchartsDataResonse(g, 0L, String.format("%.2f", 0.0));
                    statList.add(stat);
                }
            });
        } else {
            dataList.forEach(g -> {
                EchartsDataResonse stat = new EchartsDataResonse(g, 0L, String.format("%.2f", 0.0));
                statList.add(stat);
            });
        }
        return statList;
    }

    public static boolean inNumRange(int number, String numRange) {
        Objects.requireNonNull(numRange);

        if (!isValidNumRange(numRange)) {
            return false;
        }

        String[] pairs = numRange.split(",");

        // 获取开闭区间的最小值和最大值
        List<String> rangeNums = Arrays.stream(pairs).map(str -> str.replaceAll("[(|)|\\[|\\]]", "").trim()).collect(Collectors.toList());
        Integer minValue = "".equals(rangeNums.get(0)) ? null : Integer.valueOf(rangeNums.get(0));
        Integer maxValue = "".equals(rangeNums.get(1)) ? null : Integer.valueOf(rangeNums.get(1));

        // 判定数值是否大于最小值
        boolean minMatched = (minValue == null) || (pairs[0].startsWith("[") ? number >= minValue : number > minValue);
        // 判定数值是否小于最大值
        boolean maxMatched = (maxValue == null) || (pairs[1].endsWith("]") ? number <= maxValue : number < maxValue);

        return minMatched && maxMatched;
    }

    /**
     * 判断是否为有效的数字区间范围
     * @param numRange 数字区间
     * @return boolean
     */
    public static boolean isValidNumRange(String numRange) {
        return NUM_RANGE_PATTERN.matcher(numRange).matches()
                || LEFT_NUM_RANGE_PATTERN.matcher(numRange).matches()
                || RIGHT_NUM_RANGE_PATTERN.matcher(numRange).matches();
    }

    public static List<EchartsDataResonse> sum100(List<EchartsDataResonse> result) {
        if (result.isEmpty()) {
            return result;
        }
        List<EchartsDataResonse> sortedCollect = result.stream()
                .sorted(Comparator.comparing(EchartsDataResonse::getCount))
                .collect(Collectors.toList());
        if (!sortedCollect.get(0).getPer().equals(String.format("%.2f", 0.00))) {
            double sumWithoutLast = sortedCollect.stream().limit(result.size() - 1)
                    .map(v1 -> Double.valueOf(v1.getPer()))
                    .mapToDouble(Double::doubleValue)
                    .sum();
            BigDecimal sum = new BigDecimal(String.valueOf(sumWithoutLast)).setScale(3, RoundingMode.HALF_UP);
            sum = sum.setScale(2, RoundingMode.DOWN);
            String lastProportion = BigDecimal.ONE.subtract(sum).toString();

            EchartsDataResonse lastVo = sortedCollect.get(result.size() - 1);
            lastVo.setPer(String.valueOf(lastProportion));
        }
        return sortedCollect;
    }
}
