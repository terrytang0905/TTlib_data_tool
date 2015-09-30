package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBCategoryUpgradeTask {

  private static Logger logger = LoggerFactory.getLogger(DBCategoryUpgradeTask.class);

  private DB db;
  private DB newDb;
  private File categoryFile;
  private File userFile;

  /**
   * @param db
   * @param newDb
   */
  public DBCategoryUpgradeTask(String dbName1, String dbName2, File categoryFile, File userFile) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.categoryFile = categoryFile;
    this.userFile = userFile;
  }

  public void checkCategoryByFile(File missCategoryFile) {
    List<String> userList = new ArrayList<String>();
    if (missCategoryFile == null) {
      return;
    }
    int count = 0;
    int totalQueue = 0;
    BufferedReader input = null;
    BufferedWriter output = null;
    BufferedWriter output2 = null;
    try {
      input = new BufferedReader(new FileReader(missCategoryFile));
      output = new BufferedWriter(new FileWriter(categoryFile));
      output2 = new BufferedWriter(new FileWriter(userFile));
      String str = null;
      while ((str = input.readLine()) != null) {
        try {
          String userID = checkCategoryAvailible(str);
          if (userID != null) {
            output.write(str + "\r\n");
            if (!userList.contains(userID)) {
              output2.write(userID + "\r\n");
              userList.add(userID);
            }
            count++;
          }
          totalQueue++;
          System.out.println("TotalUpdateCount:" + totalQueue);
        } catch (Exception e) {
          logger.error("upgrade failure categoryID:" + str + ",because of " + e);
        }
      }
      output.flush();
      output2.flush();
    } catch (IOException e) {
      logger.error("upgrade failure (File " + missCategoryFile.getAbsolutePath() + ") because of error:" + e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
        if (output != null) {
          output.close();
        }
        if (output2 != null) {
          output2.close();
        }
      } catch (IOException e) {
        logger.error("Closing the open file failure (File " + missCategoryFile.getAbsolutePath() + ") because of error:" + e);
      }
      logger.info("Success update Category count:" + count);
    }
  }

  public String checkCategoryAvailible(String categoryID) {
    String userID = null;

    DBCollection coll1 = newDb.getCollection("ln_category");
    // DBCollection coll2 = newDb.getCollection("ln_oplog");
    DBObject queryDbo2 = (DBObject) new BasicDBObject();
    queryDbo2.put("_id", new ObjectId(categoryID));
    queryDbo2.put("status", 2);
    DBObject dbobj = coll1.findOne(queryDbo2);
    if (dbobj != null) {
      userID = (String) dbobj.get("userID");
    }
    return userID;
  }

  public void checkDefaultCategory() {
    int count = 0;
    DBCursor cursor = null;
    BufferedWriter output = null;
    try {
      output = new BufferedWriter(new FileWriter(categoryFile));
      DBCollection coll1 = db.getCollection("ln_category");
      DBCollection coll2 = newDb.getCollection("ln_category");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      List<Integer> iconList = new ArrayList<Integer>();
      iconList.add(1);
      iconList.add(2);
      iconList.add(3);
      iconList.add(4);
      iconList.add(5);
      iconList.add(6);
      queryDbo1.put("defaultCode", new BasicDBObject("$in", iconList));
      cursor = coll1.find(queryDbo1);
      while (cursor.hasNext()) {
        DBObject oldObj = cursor.next();
        ObjectId id = (ObjectId) oldObj.get("_id");
        String dataID = id.toString();

        DBObject queryObj = new BasicDBObject();
        queryObj.put("_id", id);
        queryObj.put("categoryType", 1);
        DBObject newObj = coll2.findOne();
        if (newObj != null) {
          long now = System.currentTimeMillis();
          Map<String, Object> updateMap = new HashMap<String, Object>();
          updateMap.put("categoryType", 0);
          updateMap.put("lastUpdateTime", now);
          DBObject updateSet = new BasicDBObject();
          updateSet.put("$set", updateMap);
          coll2.update(new BasicDBObject("_id", id), updateSet, false, true);

          output.write(dataID + "\r\n");
          count++;
        }
      }
      output.flush();
      logger.info("Success update Category count:" + count);
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

  public void updateCategoryV2(String userID) {
    int count = 0;
    DBCursor cursor = null;
    BufferedWriter output = null;
    try {
      output = new BufferedWriter(new FileWriter(categoryFile));
      // DBCollection coll1 = db.getCollection("ln_category");
      DBCollection coll2 = newDb.getCollection("ln_category");
      DBCollection coll3 = newDb.getCollection("ln_oplog");
      DBObject queryDbo2 = (DBObject) new BasicDBObject();
      queryDbo2.put("categoryType", 0);
      List<Integer> iconList = new ArrayList<Integer>();
      iconList.add(1);
      iconList.add(2);
      iconList.add(3);
      iconList.add(4);
      iconList.add(5);
      iconList.add(6);
      queryDbo2.put("categoryIcon", new BasicDBObject("$nin", iconList));
      if (userID != null) {
        queryDbo2.put("userID", userID);
      }
      cursor = coll2.find(queryDbo2);
      while (cursor.hasNext()) {
        DBObject newObj = cursor.next();
        ObjectId id = (ObjectId) newObj.get("_id");
        String dataID = id.toString();
        long now = System.currentTimeMillis();
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("categoryType", 1);
        updateMap.put("lastUpdateTime", now);
        DBObject updateSet = (DBObject) new BasicDBObject();
        updateSet.put("$set", updateMap);
        coll2.update(new BasicDBObject("_id", id), updateSet, false, true);

        DBObject oplogUpdate = (DBObject) new BasicDBObject();
        Map<String, Object> updateMap2 = new HashMap<String, Object>();
        updateMap2.put("createTimeStamp", now);
        updateMap2.put("timeStamp", now);
        updateMap2.put("lastUpdateTime", now);
        oplogUpdate.put("$set", updateMap2);
        coll3.update(new BasicDBObject("dataID", dataID), oplogUpdate, false, true);
        output.write(dataID + "\r\n");
        count++;
      }
      output.flush();
      logger.info("Success update Category count:" + count);
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

}
