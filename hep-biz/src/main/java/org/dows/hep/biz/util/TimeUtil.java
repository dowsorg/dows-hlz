package org.dows.hep.biz.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author jx
 * @date 2023/6/8 17:52
 */
public class TimeUtil {

    //1、时间加分钟数
    public static Date timeProcess(Date date,Integer count) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.MINUTE,count);
        Date dt1 = rightNow.getTime();
        return dt1;
    }

    //2、判断时间先后顺序
    public static boolean isBeforeTime(Date date1, Date date2) throws ParseException {
        return date1.before(date2);
    }
}
