package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class ReloadUserDataTask {

  private static Logger logger = LoggerFactory.getLogger(ReloadUserDataTask.class);
  
  private DB db;
  private DB newDb;
  private String[] collectionArray;
  private String reloadUserList=null;
  
  public ReloadUserDataTask(String dbName1, String dbName2,String reloadUserList) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.collectionArray=MongoConnectionFactory.collectionArray;
    this.reloadUserList = reloadUserList;
  }

  public void reloadUser() {
    if (reloadUserList == null) {
      return;
    }
    int count = 0;
    File reloadUseFile = new File(reloadUserList);
    DBNoteUpgradeTask upgradeTask = new DBNoteUpgradeTask(0, db, newDb, reloadUseFile);
    BufferedReader input = null;
    try {
      input = new BufferedReader(new FileReader(reloadUseFile));
      String str = null;
      while ((str = input.readLine()) != null) {
        try {
          for (String collection : collectionArray) {
            removeCollectionByUser(collection, str);
          }
          removeCollectionByUser("ln_oplog", str);
          upgradeTask.upgradeDBByUser(str);
          count++;
          logger.info("Reload User info userID:" + str);
        } catch (Exception e) {
          logger.error("remove failure userID:" + str + ",because of " + e);
        }
      }
    } catch (IOException e) {
      logger.error("remove failure (File " + reloadUseFile.getAbsolutePath() + ") because of error:" + e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
        logger.error("Closing the open file failure (File " + reloadUseFile.getAbsolutePath() + ") because of error:" + e);
      }
      String notification =
          "Round 0:" + Thread.currentThread().getName() + ",ReloadUser User Data from " + reloadUseFile.getAbsolutePath()
              + ",Total import user count:" + count;
      logger.info(notification);
    }
  }


  private void removeCollectionByUser(String collection, String userID) {
    DBCollection coll = newDb.getCollection(collection);
    DBObject queryDbo1 = (DBObject) new BasicDBObject();
    queryDbo1.put("userID", userID);
    int num = coll.remove(queryDbo1, WriteConcern.SAFE).getN();
    if (num > 0) {
      logger.debug("remove collection " + collection + " when userID=" + userID);
    }
  }
}
