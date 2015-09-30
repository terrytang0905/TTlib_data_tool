package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.db.mongo.MongoQueryExecutor;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.transfer.utils.DBTransformUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class UserDataIncrementTask {
  private static Logger logger = LoggerFactory.getLogger(UserDataIncrementTask.class);

  private DB db;
  private DB newDb;
  private String[] collectionArray;
  private MongoQueryExecutor queryExecutor;
  private String reloadUserList;
  private int incrementUserCount = 0;

  private static AtomicInteger executeUserCount = new AtomicInteger(0);
  private static ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 1000, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100),
      new ThreadPoolExecutor.DiscardOldestPolicy());
  private int round = 0;
  private static int defaultLimitNum = 500;

  /**
   * @param db
   * @param newDb
   */
  public UserDataIncrementTask(String dbName1, String dbName2, String reloadUserList) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.collectionArray = MongoConnectionFactory.collectionArray;
    this.queryExecutor = new MongoQueryExecutor();
    this.reloadUserList = reloadUserList;
  }

  public void checkReloadUser(Long startTime) {
    incrementImportUserV2(startTime);
    statisticsIncrementOplogUser(startTime);
    logger.info("Success New User & Update User count:" + incrementUserCount);

    File dirFile = new File(reloadUserList);
    File[] files = dirFile.listFiles();
    int fileSize = files.length;
    for (int i = 0; i < fileSize; i++) {
      pool.execute(new ReloadUserThread(files[i]));
    }
  }

  public void incrementImportUserV2(Long startTime) {
    File reloadUserFile = null;
    BufferedWriter output = null;

    int count = 0;
    DBCollection coll = db.getCollection("ln_user");
    DBCollection coll2 = db.getCollection("ln_oplog_counter");
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("lastLoginTime", new BasicDBObject("$gt", startTime));
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject userJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        DBObject queryDbo2 = (DBObject) new BasicDBObject();
        String userID = userJson.getString("_id");
        queryDbo2.put("userID", userID);
        DBObject oplogcounter = MongoDBUtilizer.translateId2String(coll2.findOne(queryDbo2));
        JSONObject oplogJson = JSONObject.fromObject(oplogcounter);
        Map<String, Object> map = DBTransformUtils.transferUserObject(userJson, oplogJson);
        // mapList.add(map);
        queryExecutor.insertData(newDb, "ln_user", map);
        if (incrementUserCount % defaultLimitNum == 0) {
          round = incrementUserCount / defaultLimitNum;
          if (output != null) {
            output.flush();
            output.close();
          }
          reloadUserFile = new File(reloadUserList + File.separator + "reloadUser_" + round + ".txt");
          output = new BufferedWriter(new FileWriter(reloadUserFile));
        }
        output.write(userID + "\r\n");
        incrementUserCount++;
        count++;
      }
      if (output != null)
        output.flush();
      logger.info("Success Insert new User count:" + count);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (cursor != null)
        cursor.close();
      try {
        output.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void statisticsIncrementOplogUser(Long startTime) {
    File reloadUserFile = null;
    BufferedWriter output = null;

    DBCollection coll = db.getCollection("ln_oplog");
    DBCollection coll2 = db.getCollection("ln_user");
    int count = 0;
    try {
      DBObject match = new BasicDBObject("$match", new BasicDBObject("sysTime", new BasicDBObject("$gt", startTime)));
      DBObject groupFields = new BasicDBObject("_id", "$operator");
      groupFields.put("total", new BasicDBObject("$sum", 1));
      DBObject group = new BasicDBObject("$group", groupFields);
      AggregationOutput aggrOutput = coll.aggregate(match, group);
      Iterable<DBObject> dbObjects = aggrOutput.results();
      Iterator<DBObject> dbIter = dbObjects.iterator();
      while (dbIter.hasNext()) {
        DBObject result = dbIter.next();
        String userID = (String) result.get("_id");
        DBObject queryDbo2 = (DBObject) new BasicDBObject();
        queryDbo2.put("_id", new ObjectId(userID));
        queryDbo2.put("lastLoginTime", new BasicDBObject("$lte", startTime));
        DBObject oldDbo = coll2.findOne(queryDbo2);
        if (oldDbo != null) {
          Map<String, Object> updateMap = new HashMap<String, Object>();
          updateMap.put("usedSpace", oldDbo.get("usedSpace"));
          queryExecutor.updateData(newDb, "ln_user", new BasicDBObject("_id", new ObjectId(userID)), updateMap);

          if (incrementUserCount % defaultLimitNum == 0) {
            round = incrementUserCount / defaultLimitNum;
            if (output != null) {
              output.flush();
              output.close();
            }
            reloadUserFile = new File(reloadUserList + File.separator + "reloadUser_" + round + ".txt");
            output = new BufferedWriter(new FileWriter(reloadUserFile));
          } else if (output == null) {
            reloadUserFile = new File(reloadUserList + File.separator + "reloadUser_" + round + ".txt");
            output = new BufferedWriter(new FileWriter(reloadUserFile));
          }
          output.write(userID + "\r\n");
          incrementUserCount++;
          count++;
        }
      }
      if (output != null)
        output.flush();
      logger.info("Success Update the current User count:" + count);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        output.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  class ReloadUserThread implements Runnable {

    private File reloadUserFile = null;
    
    public ReloadUserThread(File reloadUserFile) {
      this.reloadUserFile = reloadUserFile;
    }

    @Override
    public void run() {
      // TODO Auto-generated method stub
      reloadUser();
    }

    public void reloadUser() {
      int count = 0;
      DBNoteUpgradeTask upgradeTask = new DBNoteUpgradeTask(0, db, newDb, reloadUserFile);
      BufferedReader input = null;
      try {
        input = new BufferedReader(new FileReader(reloadUserFile));
        String str = null;
        while ((str = input.readLine()) != null) {
          try {
            for (String collection : collectionArray) {
              removeCollectionByUser(collection, str);
            }
            removeCollectionByUser("ln_oplog", str);
            upgradeTask.upgradeDBByUser(str);
            count++;
            executeUserCount.addAndGet(count);
          } catch (Exception e) {
            logger.error("reloadUser failure userID:" + str + ",because of " + e);
          }
        }
      } catch (IOException e) {
        logger.error("reloadUser failure (File " + reloadUserFile.getAbsolutePath() + ") because of error:" + e);
      } finally {
        try {
          if (input != null) {
            input.close();
          }
        } catch (IOException e) {
          logger.error("Closing the open file failure (File " + reloadUserFile.getAbsolutePath() + ") because of error:" + e);
        }
        String notification =
            "Round 0:" + Thread.currentThread().getName() + ",ReloadUser User Data from " + reloadUserFile.getAbsolutePath()
                + ",Total import user count:" + executeUserCount.get();
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


}
