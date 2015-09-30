package com.newroad.data.transfer.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.transfer.upgrade.DataIncrementTask;

public class GTDBTransferAction {
  private static Logger logger = LoggerFactory.getLogger(GTDBTransferAction.class);
  
  /**
   * @Title: main
   * @Description: TODO
   * @param @param args
   * @return void
   * @throws
   */
  public static void main(String[] args) {
    
    long start=System.currentTimeMillis();
    logger.info("Start greenTea increment upgrade data V2 task at time:"+start);
    DataIncrementTask dataIncrementTask= new DataIncrementTask(MongoConnectionFactory.dbName1, MongoConnectionFactory.dbName2);
    dataIncrementTask.incrementGreenTeaUpgradeDB();
    long end=System.currentTimeMillis();
    logger.info("End greenTea increment upgrade data V2 task at time:"+(end-start));
  }
}
