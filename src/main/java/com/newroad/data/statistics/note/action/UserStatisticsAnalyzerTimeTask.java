package com.newroad.data.statistics.note.action;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.statistics.datamodel.OverAllStatistics;
import com.newroad.data.common.db.mysql.MySqlDao;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;

public class UserStatisticsAnalyzerTimeTask extends TimerTask {

  private static final Logger logger = LoggerFactory.getLogger(UserStatisticsAnalyzerTimeTask.class);

  private MySqlDao mysqlDao;

  public UserStatisticsAnalyzerTimeTask() {
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
    Long lastStatisticsTimeMs = getLastStatisticsTime();
    Long statisticsTime = System.currentTimeMillis();
    logger.info("                ");
    logger.info("Data statistics task covers from start time " + new Date(lastStatisticsTimeMs)
        + " to end time " + new Date(statisticsTime));

    UserStatisticsAnalyzer userAnalyzer =
        new UserStatisticsAnalyzer(lastStatisticsTimeMs, statisticsTime, mysqlDao);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.regUserFilePath);
//    userAnalyzer.regUserSyncStatistics();
//    
//    userAnalyzer.userCommonStatistics(OperateType.install);
//    userAnalyzer.userCommonStatistics(OperateType.activate);

//    DataExceptionHandler.initLogFile(DataExceptionHandler.startFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.start);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.stopFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.stop);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.loginFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.login);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.logoutFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.logout);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.syncFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.synchronize);
//    DataExceptionHandler.initLogFile(DataExceptionHandler.commitFilePath);
//    userAnalyzer.userCommonStatistics(OperateType.commit);

    userAnalyzer.analyzeDeviceStatistics();
    userAnalyzer.analyzeRegUserLoginStatistics();
    userAnalyzer.analyzeRegUserBehaviorStatistics();
    userAnalyzer.analyzeUserDayStatistics();
    userAnalyzer.overAllDataStatistics();

//    MessageDataAnalyzer messageAnalyzer=new MessageDataAnalyzer(mysqlDao);
//    messageAnalyzer.viewMessageGroupQuery("122");
  }

  private Long getLastStatisticsTime() {
    OverAllStatistics last =
        (OverAllStatistics)mysqlDao.selectOne(
            NoteUserStatisticsConstants.GET_MAX_OVERALL_STATISTICS, null);
    return last == null ? 0L : last.getStatisticsTime().getTime();
  }

}
