package com.nightssky.datatrendgraph.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2017/3/7.
 */

public class TimeUtils {
    /**
     * @return  当前年月日
     * yyyy-MM-dd日    HH:mm:ss
     */
    public static String getCurrentDate(){
        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy-MM-dd");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        return str;
    }
    public static String[] getWeekDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar c = Calendar.getInstance();
        String[] weekDay = new String[7];

        for (int i=0;i<7;i++){
            try {
                c.add(Calendar.DATE, -1);
                Date monday = c.getTime();
                String preMonday = sdf.format(monday);
                weekDay[6-i] = preMonday;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return weekDay;
    }
}
