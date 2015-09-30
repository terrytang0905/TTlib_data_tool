package com.newroad.data.statistics.yoyo.executor;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mysql.MySqlDao;


public class YoyoStatisticsAnalyzerTimeTask extends TimerTask{

  
  private static final Logger logger = LoggerFactory.getLogger(YoyoStatisticsAnalyzerTimeTask.class);

  private MySqlDao mysqlDao;

  public YoyoStatisticsAnalyzerTimeTask() {
    this.mysqlDao = new MySqlDao();
  }

  @Override
  public void run() {
    statisticsTask();
  }

  /**
   * 执行任务
   */
  public synchronized void statisticsTask() {
    //Long lastStatisticsTimeMs = getLastStatisticsTime();
    Long statisticsTime = System.currentTimeMillis();

    YoyoStatisticsAnalyzer userAnalyzer =
        new YoyoStatisticsAnalyzer(0l, statisticsTime);
    
//    userAnalyzer.analyzeDeviceStatistics();
//    userAnalyzer.analyzeRegUserLoginStatistics();
//    userAnalyzer.analyzeRegUserBehaviorStatistics();
//    userAnalyzer.analyzeUserDayStatistics();
    userAnalyzer.overAllDataStatistics();
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    YoyoStatisticsAnalyzerTimeTask timeTask=new YoyoStatisticsAnalyzerTimeTask();
    timeTask.statisticsTask();
  }



}
