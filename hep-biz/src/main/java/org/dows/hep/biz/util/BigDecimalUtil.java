package org.dows.hep.biz.util;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/5/17 18:19
 */
public class BigDecimalUtil {
    final static int s_commonDivideScale=8;
    public static final BigDecimal ONEHundred=new BigDecimal("100");
    //region parse
    public static Short shortValue(BigDecimal src) {
        return shortValue(src,null);
    }
    public static Short shortValue(BigDecimal src,Short dft) {
        return null == src ? dft : src.shortValue();
    }
    public static Integer intValue(BigDecimal src) {
        return intValue(src,null);
    }
    public static Integer intValue(BigDecimal src,Short dft) {
        return null == src ? dft : src.intValue();
    }
    public static Long longValue(BigDecimal src) {
        return longValue(src,null);
    }
    public static Long longValue(BigDecimal src, Long dft) {
        return null == src ? dft : src.longValue();
    }
    public static Double doubleValue(BigDecimal src) {
        return doubleValue(src,null);
    }
    public static Double doubleValue(BigDecimal src, Double dft) {
        return null == src ? dft : src.doubleValue();
    }

    public static BigDecimal valueOf(Object src){
        return valueOf(src,null);
    }
    public static BigDecimal valueOf(Object src,BigDecimal dft){
        if(null==src){
            return dft;
        }
        if(src instanceof BigDecimal){
            return (BigDecimal) src;
        }
        return new BigDecimal(src.toString());
    }
    public static BigDecimal valueOf(Short src){
        return valueOf(src,null);
    }
    public static BigDecimal valueOf(Short src,BigDecimal dft){
        return null==src?dft:BigDecimal.valueOf(src.longValue());
    }
    public static BigDecimal valueOf(Integer src){
        return valueOf(src,null);
    }
    public static BigDecimal valueOf(Integer src,BigDecimal dft){
        return null==src?dft:BigDecimal.valueOf(src.longValue());
    }
    public static BigDecimal valueOf(Long src){
        return valueOf(src,null);
    }
    public static BigDecimal valueOf(Long src,BigDecimal dft){
        return null==src?dft:BigDecimal.valueOf(src.longValue());
    }
    public static BigDecimal valueOf(Double src){
        return valueOf(src,null);
    }
    public static BigDecimal valueOf(Double src,BigDecimal dft){
        return null==src?dft:new BigDecimal(src.toString());
    }

    public static BigDecimal parseDecimal(Number src){
        return NumberUtil.toBigDecimal(src);
    }
    public static BigDecimal tryParseDecimalElseNull(String src){
        return tryParseDecimal(src,null);
    }
    public static BigDecimal tryParseDecimalElseZero(String src){
        return tryParseDecimal(src,BigDecimal.ZERO);
    }

    public static BigDecimal tryParseDecimal(String src, BigDecimal dft) {
        BigDecimal rst = dft;
        try {
            if(!ShareUtil.XObject.isNumber(src)){
                return rst;
            }
            if (ShareUtil.XString.hasLength(src)) {
                rst =new BigDecimal(src.trim());
            }
        } catch (Exception ex) {

        }
        return rst;
    }

    //endregion


    //region format
    public static String formatDecimal(BigDecimal src) {
        return formatDecimal(src, null);
    }

    public static String formatDecimal(BigDecimal src, String dft) {
        return src == null ? dft : src.toPlainString();
    }
    public static String formatPercent(BigDecimal src, int scale, boolean multi100) {
        return formatPercent(src, "", scale, multi100, "%");
    }

    public static String formatPercent(BigDecimal src, String dft, int scale, boolean multi100) {
        return formatPercent(src, dft, scale, multi100, "%");
    }

    public static String formatPercent(BigDecimal src, String dft, int scale, boolean multi100, String suffix) {
        if (null == src) {
            return dft;
        }
        if (multi100) {
            src = src.multiply(BigDecimal.valueOf(100));
        }
        if (scale > 0) {
            src = src.setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
        return String.format("%s%s", src.toPlainString(), null == suffix ? "" : suffix);
    }

    //endregion

    //region round
    public static BigDecimal roundDecimal(BigDecimal src, int scale) {
        return roundDecimal(src, scale,false,null);
    }
    public static BigDecimal roundDecimal(BigDecimal src, int scale,boolean stripZero){
        return roundDecimal(src, scale,stripZero,null);
    }
    public static BigDecimal roundDecimal(BigDecimal src, int scale,boolean stripZero,BigDecimal dft) {
        if(null==src){
            return dft;
        }
        BigDecimal rst=src.setScale(scale, BigDecimal.ROUND_HALF_UP);
        if(stripZero){
            rst=rst.stripTrailingZeros();
        }
        return rst;
    }
    public static String formatRoundDecimal(BigDecimal src, int scale){
        return formatRoundDecimal(src, scale, false,null);
    }
    public static String formatRoundDecimal(BigDecimal src, int scale,boolean stripZero) {
        return formatRoundDecimal(src, scale, stripZero,null);
    }

    public static String formatRoundDecimal(BigDecimal src, int scale,boolean stripZero,  String dft) {
        if(null==src){
            return dft;
        }
        return roundDecimal(src,scale,stripZero).toPlainString();
    }
    //endregion

    //region add
    //遇null返回
    public static BigDecimal addOptional(BigDecimal x,BigDecimal y){
        return add(x,y,-1,false);
    }
    public static BigDecimal addOptional(BigDecimal x,BigDecimal y,int scale){
        return add(x,y,scale,false);
    }
    //遇null视为0
    public static BigDecimal add(BigDecimal x,BigDecimal y){
        return add(x,y,-1,true);
    }
    public static BigDecimal add(BigDecimal x,BigDecimal y,int scale){
        return add(x,y,scale,true);
    }
    private static BigDecimal add(BigDecimal x,BigDecimal y,int scale, boolean nullAsZero){
        if (!nullAsZero && (null == x || null == y)) {
            return null;
        }
        x = Optional.ofNullable(x).orElse(BigDecimal.ZERO);
        y = Optional.ofNullable(y).orElse(BigDecimal.ZERO);
        BigDecimal val=x.add(y);
        return scale<0?val:val.setScale(scale,BigDecimal.ROUND_HALF_UP);
    }
    //endregion

    //region subtract
    //遇null返回
    public static BigDecimal subOptional(BigDecimal x,BigDecimal y){
        return sub(x,y,-1,false);
    }
    public static BigDecimal subOptional(BigDecimal x,BigDecimal y,int scale){
        return sub(x,y,scale,false);
    }
    //遇null视为0
    public static BigDecimal sub(BigDecimal x,BigDecimal y){
        return sub(x,y,-1,true);
    }
    public static BigDecimal sub(BigDecimal x,BigDecimal y,int scale){
        return sub(x,y,scale,true);
    }
    private static BigDecimal sub(BigDecimal x,BigDecimal y,int scale, boolean nullAsZero){
        if (!nullAsZero && (null == x || null == y)) {
            return null;
        }
        x = Optional.ofNullable(x).orElse(BigDecimal.ZERO);
        y = Optional.ofNullable(y).orElse(BigDecimal.ZERO);
        BigDecimal val=x.subtract(y);
        return scale<0?val:val.setScale(scale,BigDecimal.ROUND_HALF_UP);
    }
    //endregion


    //region multiply
    //遇null返回
    public static BigDecimal mulOptional(BigDecimal x,BigDecimal y){
        return mul(x,y,-1,false);
    }
    public static BigDecimal mulOptional(BigDecimal x,BigDecimal y,int scale){
        return mul(x,y,scale,false);
    }
    //遇null视为0
    public static BigDecimal mul(BigDecimal x,BigDecimal y){
        return mul(x,y,-1,true);
    }
    public static BigDecimal mul(BigDecimal x,BigDecimal y,int scale){
        return mul(x,y,scale,true);
    }
    private static BigDecimal mul(BigDecimal x,BigDecimal y,int scale, boolean nullAsZero){
        if (!nullAsZero && (null == x || null == y)) {
            return null;
        }
        x = Optional.ofNullable(x).orElse(BigDecimal.ZERO);
        y = Optional.ofNullable(y).orElse(BigDecimal.ZERO);
        BigDecimal val=x.multiply(y);
        return scale<0?val:val.setScale(scale,BigDecimal.ROUND_HALF_UP);
    }
    //endregion

    //region divide
    //遇null返回
    public static BigDecimal divOptional(BigDecimal x,BigDecimal y){
        return div(x,y,-1,false);
    }
    public static BigDecimal divOptional(BigDecimal x,BigDecimal y,int scale){
        return div(x,y,scale,false);
    }
    //遇null视为0
    public static BigDecimal div(BigDecimal x,BigDecimal y){
        return div(x,y,-1,true);
    }
    public static BigDecimal div(BigDecimal x,BigDecimal y,int scale){
        return div(x,y,scale,true);
    }
    private static BigDecimal div(BigDecimal x, BigDecimal y, int scale, boolean nullAsZero) {
        if (!nullAsZero && (null == x || null == y)) {
            return null;
        }
        x = Optional.ofNullable(x).orElse(BigDecimal.ZERO);
        y = Optional.ofNullable(y).orElse(BigDecimal.ZERO);
        if (y.compareTo(BigDecimal.ZERO) == 0)
            return null;
        return x.divide(y, scale < 0 ? s_commonDivideScale : scale, RoundingMode.HALF_UP);
    }
    //endregion

    //region compare
    public static BigDecimal max(BigDecimal x,BigDecimal y){
        if(null==y)
            return x;
        if(null==x)
            return y;
        return x.compareTo(y)>=0?x:y;
    }
    public static BigDecimal min(BigDecimal x,BigDecimal y){
        if(null==y)
            return y;
        if(null==x)
            return x;
        return x.compareTo(y)<=0?x:y;
    }
    //endregion
}
