package com.newroad.data.statistics.exception;

import java.io.FileWriter;
import java.io.IOException;

import com.newroad.data.statistics.note.task.RegUserSyncTask;
import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.db.mysql.MySqlDao;
import com.mongodb.DB;

public class DataExceptionHandler {

  public static String filePath = "error.log";
  public static String regUserFilePath = "regUser.log";
  public static String startFilePath = "startOperation.log";
  public static String stopFilePath = "stopOperation.log";
  public static String loginFilePath = "loginOperation.log";
  public static String logoutFilePath = "logoutOperation.log";
  public static String syncFilePath = "syncOperation.log";
  public static String commitFilePath = "commitOperation.log";

  private static FileWriter log;

  private DB db;
  private MySqlDao mysqlDao;

  public static void initLogFile(String filePath) {
    try {
      log = new FileWriter(filePath);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void writeFile(String info) {
    if (log != null) {
      try {
        log.write(info + "\r\n");
        log.flush();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  public static void main(String[] args) {
    RegUserSyncTask regTask = new RegUserSyncTask(MongoConnectionFactory.getDefaultDB(), new MySqlDao());
    regTask.registerUserStatistics("54504e750cf20a2247540fac");
  }
}
