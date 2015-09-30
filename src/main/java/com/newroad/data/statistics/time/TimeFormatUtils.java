package com.newroad.data.statistics.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtils {

  public static String dateFormat(Long dateLong) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String date = formatter.format(new Date(dateLong));
    System.out.println("date:" + date);
    return date;
  }

  public static String dateTimeFormat(Long dateLong) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = formatter.format(new Date(dateLong));
    System.out.println("dateTime:" + date);
    return date;
  }
  
  public static Long[] generateDateLongFormat(String startTime,String endTime){
    Long[] dateInterval=new Long[2];
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date startDate = formatter.parse(startTime);
      Long dateLong = startDate.getTime();
      dateInterval[0]=dateLong;
      Date endDate = formatter.parse(endTime);
      Long date2Long = endDate.getTime();
      dateInterval[1]=date2Long;
      System.out.println("Generate Date(LongFormat) StartTime:" + dateLong + ",EndTime:" + date2Long);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dateInterval;
  }
  
  public static void main(String[] args) {
    String today = "2014-07-01 00:00:00";
    String today2 = "2014-07-02 23:59:59";
//    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    // System.out.println(formatter.format(new Date(1408589473334L)));
//    // String date = formatter.format(new Date(1408589552490L));
//    // System.out.println("V1 lastTime:"+date);
//    try {
//      Date date1 = formatter.parse(today);
//      long dateLong = date1.getTime();
//      Date date2 = formatter.parse(today2);
//      long date2Long = date2.getTime();
//      System.out.println("V2 Time Start:" + dateLong + ",End:" + date2Long);
//    } catch (ParseException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    generateDateLongFormat(today,today2);
    dateTimeFormat(1405083744703L);
    
  }

}
