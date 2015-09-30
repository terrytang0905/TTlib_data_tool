package com.newroad.data.statistics.note.action;


public class UserStatisticsExecutor {

  public static void main(String[] args) {
//    TimerTask statisticsTask = new DataAnalyzerTimeTask();
//    Timer timer = new Timer();
//    timer.scheduleAtFixedRate(statisticsTask, 0, 1000 * 60 * 60 * 24);
    UserStatisticsAnalyzerTimeTask timeTask=new UserStatisticsAnalyzerTimeTask();
    timeTask.statisticsTask();
  }

}
