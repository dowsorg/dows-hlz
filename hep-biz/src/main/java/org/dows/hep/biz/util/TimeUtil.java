package org.dows.hep.biz.util;

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
}
