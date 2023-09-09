package org.dows.hep.biz.eval.codec;

import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : wuzl
 * @date : 2023/9/6 15:14
 */
public interface IRDStringCodec<T> {
    T fromRDString(String str);

    default String toRDString(T obj){
        StringBuilder sb=new StringBuilder();
        appendRDString(sb, obj);
        String rst=sb.toString();
        sb.delete(0, sb.length());
        return rst;
    }

    void appendRDString(StringBuilder sb,T obj);

    default String toString(Object obj){
        return null==obj?"":obj.toString();
    }
    default String toString(Date obj){
        return null==obj?"":String.valueOf( obj.getTime());
    }

    default String toString(BigDecimal obj){
        return null==obj?"": BigDecimalUtil.formatRoundDecimal(obj, 2);
    }

    default String toString(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return vals[idx];
    }

    default Long toLong(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return toLong(vals[idx]);
    }
    default Long toLong(String str){
        return ShareUtil.XObject.notNumber(str)?null:Long.valueOf(str);
    }

    default Integer toInteger(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return toInteger(vals[idx]);
    }
    default Integer toInteger(String str){
        return ShareUtil.XObject.notNumber(str)?null:Integer.valueOf(str);
    }

    default Byte toByte(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return toByte(vals[idx]);
    }
    default Byte toByte(String str){
        return ShareUtil.XObject.notNumber(str)?null:Byte.valueOf(str);
    }

    default BigDecimal toBigDecimal(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return toBigDecimal(vals[idx]);
    }

    default BigDecimal toBigDecimal(String str){
        if(null==str){
            return null;
        }
        return BigDecimalUtil.tryParseDecimalElseNull(str);
    }
    default Date toDate(String[] vals, int idx){
        if(idx>=vals.length){
            return null;
        }
        return toDate(vals[idx]);
    }
    default Date toDate(String str){
        if(ShareUtil.XObject.isEmpty(str)){
            return null;
        }
        Long ts=toLong(str);
        if(null==ts){
            return null;
        }
        return new Date(ts);
    }




}
