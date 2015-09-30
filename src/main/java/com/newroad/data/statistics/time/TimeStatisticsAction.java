package com.newroad.data.statistics.time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TimeStatisticsAction {

  public void transferCSVFile(String csvFile, String updateCSVFile) {
    BufferedReader reader = null;
    BufferedWriter writer = null;
    Map<String, Integer> channelCount = new HashMap<String, Integer>();
    try {
      reader = new BufferedReader(new FileReader(csvFile));// 换成你的文件名
      writer = new BufferedWriter(new FileWriter(updateCSVFile, true));
      reader.readLine();// 第一行信息，为标题信息，不用，如果需要，注释掉
      String line = null;
      while ((line = reader.readLine()) != null) {
        String[] items = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
        String channel = items[0];
        String dataLongStr = items[1];
        Long dateLong = Long.valueOf(dataLongStr);
        String dateStr = TimeFormatUtils.dateFormat(dateLong);
        String updateInfo = channel + "_" + dateStr;
        Integer count = channelCount.get(updateInfo);
        if (count != null) {
          count++;
          channelCount.put(updateInfo, count);
        } else {
          channelCount.put(updateInfo, 1);
        }
      }
      Set<Entry<String,Integer>> set=channelCount.entrySet();
      Iterator<Entry<String,Integer>> iter=set.iterator();
      while(iter.hasNext()){
        Entry<String,Integer> entry=iter.next();
        writer.write(entry.getKey()+":"+entry.getValue());
        writer.newLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        writer.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    TimeStatisticsAction tfx = new TimeStatisticsAction();
    for (String csvFile : args) {
      int index = csvFile.indexOf(".csv");
      String updateCSVFilePath = csvFile.substring(0, index) + "_update2" + csvFile.substring(index);
      tfx.transferCSVFile(csvFile, updateCSVFilePath);
    }
  }

}
