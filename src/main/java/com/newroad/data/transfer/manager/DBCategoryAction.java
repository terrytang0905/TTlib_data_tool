package com.newroad.data.transfer.manager;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.utils.PropertiesUtils;
import com.newroad.data.transfer.upgrade.DBCategoryUpgradeTask;

public class DBCategoryAction {

  private static Logger logger = LoggerFactory.getLogger(DBCategoryAction.class);

  public static void main(String[] args) {
    // DBDictionaryUpgrade dbDict = new DBDictionaryUpgrade();
    // dbDict.upgradeDictionaryV2();
    long start = System.currentTimeMillis();
    logger.info("Start category update V2 task at time:" + start);
    File missCategoryFile = new File(PropertiesUtils.missListFilePath);
    File dataList = new File(PropertiesUtils.userListFolderPath);
    File userList = new File(PropertiesUtils.errorListFilePath);
    DBCategoryUpgradeTask dbCategory = new DBCategoryUpgradeTask(MongoConnectionFactory.dbName1, MongoConnectionFactory.dbName2, missCategoryFile, userList);
    dbCategory.checkCategoryByFile(dataList);
    long end = System.currentTimeMillis();
    logger.info("End category update V2 task at time:" + end);
    // dbNote.checkData();
  }
}
