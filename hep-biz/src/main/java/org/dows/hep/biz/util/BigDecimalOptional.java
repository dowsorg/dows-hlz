package org.dows.hep.biz.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author : wuzl
 * @date : 2023/5/17 18:09
 */
public class BigDecimalOptional {
    private BigDecimal value;

    protected BigDecimalOptional(BigDecimal val){
        this.value=val;
    }

    public static BigDecimalOptional create(){
        return new BigDecimalOptional(null);
    }
    public static BigDecimalOptional zero(){
        return new BigDecimalOptional(BigDecimal.ZERO);
    }

    public static BigDecimalOptional valueOf(Short val){
        return new BigDecimalOptional(BigDecimalUtil.valueOf(val,null));
    }
    public static BigDecimalOptional valueOf(Integer val){
        return new BigDecimalOptional(BigDecimalUtil.valueOf(val,null));
    }
    public static BigDecimalOptional valueOf(Long val){
        return new BigDecimalOptional(BigDecimalUtil.valueOf(val,null));
    }
    public static BigDecimalOptional valueOf(Double val){
        return new BigDecimalOptional(BigDecimalUtil.valueOf(val,null));
    }
    public static BigDecimalOptional valueOf(BigDecimal val){
        return new BigDecimalOptional(val);
    }

    public static BigDecimalOptional valueOf(String str){
        return new BigDecimalOptional(BigDecimalUtil.tryParseDecimalElseNull(str));
    }

    public BigDecimal getValue(){
        return this.value;
    }
    public BigDecimal getValue(int scale){
        if(null==this.value) {
            return null;
        }
        return BigDecimalUtil.roundDecimal(this.value,scale);
    }
    public BigDecimal getValue(int scale, RoundingMode roundingMode){
        if(null==this.value) {
            return null;
        }
        return BigDecimalUtil.roundDecimal(this.value,scale,roundingMode);
    }
    public String getString(int scale){
       return getString(scale,RoundingMode.HALF_UP);
    }
    public String getString(int scale, RoundingMode roundingMode){
        if(null==this.value) {
            return null;
        }
        return BigDecimalUtil.formatRoundDecimal(this.value,scale,roundingMode);
    }

    public BigDecimalOptional setValue(BigDecimal val){
        this.value=val;
        return this;
    }
    public BigDecimalOptional reset(){
        this.value=null;
        return this;
    }

    public boolean isPresent(){
        return null!=value;
    }
    public boolean isEmpty() {
        return null == value;
    }

    //region add
    public BigDecimalOptional addOptional(BigDecimal other){
        value=BigDecimalUtil.addOptional(value,other);
        return this;
    }
    public BigDecimalOptional addOptional(BigDecimal other, int scale){
        value=BigDecimalUtil.addOptional(value,other,scale);
        return this;
    }
    public BigDecimalOptional add(BigDecimal other){
        value=BigDecimalUtil.add(value,other);
        return this;
    }
    public BigDecimalOptional add(BigDecimal other, int scale){
        value=BigDecimalUtil.add(value,other,scale);
        return this;
    }
    //endregion

    //region add
    public BigDecimalOptional subOptional(BigDecimal other){
        value=BigDecimalUtil.subOptional(value,other);
        return this;
    }
    public BigDecimalOptional subOptional(BigDecimal other, int scale){
        value=BigDecimalUtil.subOptional(value,other,scale);
        return this;
    }
    public BigDecimalOptional sub(BigDecimal other){
        value=BigDecimalUtil.sub(value,other);
        return this;
    }
    public BigDecimalOptional sub(BigDecimal other, int scale){
        value=BigDecimalUtil.sub(value,other,scale);
        return this;
    }
    //endregion


    //region multiply
    public BigDecimalOptional mulOptional(BigDecimal other){
        value=BigDecimalUtil.mulOptional(value,other);
        return this;
    }
    public BigDecimalOptional mulOptional(BigDecimal other, int scale){
        value=BigDecimalUtil.mulOptional(value,other,scale);
        return this;
    }
    public BigDecimalOptional mul(BigDecimal other){
        value=BigDecimalUtil.mul(value,other);
        return this;
    }
    public BigDecimalOptional mul(BigDecimal other, int scale){
        value=BigDecimalUtil.mul(value,other,scale);
        return this;
    }
    //endregion

    //region divide
    public BigDecimalOptional divOptional(BigDecimal other){
        BigDecimal v=BigDecimalUtil.divOptional(value,other);
        if(null!=v){
            value=v;
        }
        return this;
    }
    public BigDecimalOptional divOptional(BigDecimal other, int scale) {
        BigDecimal v=BigDecimalUtil.divOptional(value, other, scale);
        if(null!=v){
            value=v;
        }
        return this;
    }
    public BigDecimalOptional divOptional(BigDecimal other, int scale,RoundingMode roundingMode){
        BigDecimal v=BigDecimalUtil.divOptional(value,other,scale,roundingMode);
        if(null!=v){
            value=v;
        }
        return this;
    }
    public BigDecimalOptional div(BigDecimal other){
        BigDecimal v=BigDecimalUtil.div(value,other);
        if(null!=v){
            value=v;
        }
        return this;
    }
    public BigDecimalOptional div(BigDecimal other, int scale){
        BigDecimal v=BigDecimalUtil.div(value,other,scale);
        if(null!=v){
            value=v;
        }
        return this;
    }
    public BigDecimalOptional div(BigDecimal other, int scale,RoundingMode roundingMode) {
        BigDecimal v = BigDecimalUtil.div(value, other, scale, roundingMode);
        if(null!=v){
            value=v;
        }
        return this;
    }
    //endregion


    //region min-max
    public BigDecimalOptional min(BigDecimal other){
        if(null==other){
            return this;
        }
        if(this.isEmpty()){
            value=other;
            return this;
        }
        if(value.compareTo(other)<0){
            value=other;
        }
        return this;
    }
    public BigDecimalOptional max(BigDecimal other){
        if(null==other){
            return this;
        }
        if(this.isEmpty()){
            value=other;
            return this;
        }
        if(other.compareTo(value)<0){
            value=other;
        }
        return this;
    }
    //endregion
}
