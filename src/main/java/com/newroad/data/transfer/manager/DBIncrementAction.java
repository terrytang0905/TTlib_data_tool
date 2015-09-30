package com.newroad.data.transfer.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.utils.PropertiesUtils;
import com.newroad.data.transfer.upgrade.DataIncrementTask;

public class DBIncrementAction {

  private static Logger logger = LoggerFactory.getLogger(DBIncrementAction.class);

  /**
   * @Title: data upgrade stage 2
   * @Description: upgrade the new increment data for the current day
   * @param @param args
   * @return void
   * @throws
   */
  public static void main(String[] args) {
    long dateLong = 0l;
    String today = PropertiesUtils.incrementTime;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date;
    try {
      date = formatter.parse(today);
      dateLong = date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    long start = System.currentTimeMillis();
    logger.info("Start increment upgrade data V2 task at time:" + start);
    DataIncrementTask dataIncrementTask =
        new DataIncrementTask(MongoConnectionFactory.dbName1, MongoConnectionFactory.dbName2,
            PropertiesUtils.reloadUserListFolderPath, PropertiesUtils.oplogDataList);
    dataIncrementTask.incrementUpgradeDBByTime(dateLong);
//    UserDataIncrementTask userIncrementTask = new UserDataIncrementTask(DBConnection.dbName1, DBConnection.dbName2,DBConnection.reloadUserListFolderPath);
//    userIncrementTask.checkReloadUser(dateLong);

    long end = System.currentTimeMillis();
    logger.info("End increment upgrade data V2 task at time:" + (end - start));
  }
}
