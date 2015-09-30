package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

/**
 * @author tangzj1
 * @version 1.0
 * 
 */
public class DataIncrementTask {

  private static Logger logger = LoggerFactory.getLogger(DataIncrementTask.class);

  private DB db;
  private DB newDb;
  private MongoQueryExecutor queryExecutor;
  private String reloadUserList;
  private String incrementOplogDataPath;
  private int incrementUserCount = 0;
  private static AtomicInteger executeOplogCount = new AtomicInteger(0);
  private static ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 1000, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100),
      new ThreadPoolExecutor.DiscardOldestPolicy());

  private static int defaultLimitNum = 1000;

  public DataIncrementTask(String dbName1, String dbName2) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
  }
  
  /**
   * @param db
   * @param newDb
   */
  public DataIncrementTask(String dbName1, String dbName2, String reloadUserList, String incrementOplogDataPath) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.queryExecutor = new MongoQueryExecutor();
    this.reloadUserList = reloadUserList;
    this.incrementOplogDataPath = incrementOplogDataPath;
  }

  public void checkReloadUser(Long startTime) {
    File reloadUserFile = new File(reloadUserList);
    BufferedWriter output = null;
    try {
      output = new BufferedWriter(new FileWriter(reloadUserFile));
      incrementImportUserV2(output, startTime);
      statisticsIncrementOplogUser(output, startTime);
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      logger.info("Success New User & Update User count:" + incrementUserCount);
    }
  }

  public void incrementImportUserV2(BufferedWriter output, Long startTime) {
    // DBCollection coll3 = newDb.getCollection("ln_user");
    // coll3.remove(new BasicDBObject(), WriteConcern.SAFE);
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
        output.write(userID + "\r\n");
        incrementUserCount++;
        count++;
      }
      logger.info("Success Insert new User count:" + count);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public void statisticsIncrementOplogUser(BufferedWriter output, Long startTime) {

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
          output.write(userID + "\r\n");
          incrementUserCount++;
          count++;
        }
      }
      logger.info("Success Update the current User count:" + count);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void updateCategoryV2(Long startTime) {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_category");
      DBCollection coll2 = db.getCollection("ln_sharelink");
      DBCollection coll3 = newDb.getCollection("ln_category");

      DBCollection coll0 = db.getCollection("ln_oplog");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("sysTime", new BasicDBObject("$gt", startTime));
      List<Integer> typeList = new ArrayList<Integer>();
      typeList.add(8);
      typeList.add(16);
      typeList.add(32);
      queryDbo1.put("type", new BasicDBObject("$in", typeList));
      cursor = coll0.find(queryDbo1);
      while (cursor.hasNext()) {
        DBObject oplogDbo = cursor.next();
        String categoryId = oplogDbo.get("dataID").toString();
        DBObject oldCategory = coll1.findOne(new BasicDBObject("_id", new ObjectId(categoryId)));
        JSONObject categoryJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(oldCategory));
        DBObject queryDbo2 = (DBObject) new BasicDBObject();
        queryDbo2.put("nodeID", categoryId);
        queryDbo2.put("nodeType", 2);
        DBObject shareObject = coll2.findOne(queryDbo2);
        JSONObject shareJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(shareObject));
        Map<String, Object> newCategoryMap = DBTransformUtils.transferCategoryObject(categoryJson, shareJson);

        coll3.findAndRemove(new BasicDBObject("_id", new ObjectId(categoryId)));
        queryExecutor.insertData(newDb, "ln_category", newCategoryMap);
        count++;
      }
      logger.info("Success Insert new Category count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public void updateCategoryV22(Long startTime) {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_category");
      DBCollection coll2 = db.getCollection("ln_sharelink");
      DBCollection coll3 = newDb.getCollection("ln_category");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("genTime", new BasicDBObject("$gt", startTime));
      cursor = coll1.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject categoryJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        String id = categoryJson.getString("_id");
        DBObject queryDbo2 = (DBObject) new BasicDBObject();
        queryDbo2.put("nodeID", id);
        queryDbo2.put("nodeType", 2);
        DBObject shareObject = coll2.findOne(queryDbo2);
        JSONObject shareJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(shareObject));
        Map<String, Object> newCategoryMap = DBTransformUtils.transferCategoryObject(categoryJson, shareJson);

        coll3.findAndRemove(new BasicDBObject("_id", new ObjectId(id)));
        queryExecutor.insertData(newDb, "ln_category", newCategoryMap);
        count++;
      }
      logger.info("Success Insert new Category count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }


  public void updateInsertNoteV2(Long startTime) {
    // List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_note");
      DBCollection coll2 = db.getCollection("ln_sharelink");
      DBCollection coll3 = newDb.getCollection("ln_note");

      DBCollection coll0 = db.getCollection("ln_oplog");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("sysTime", new BasicDBObject("$gt", startTime));
      List<Integer> typeList = new ArrayList<Integer>();
      typeList.add(1);
      typeList.add(2);
      typeList.add(4);
      queryDbo1.put("type", new BasicDBObject("$in", typeList));
      cursor = coll0.find(queryDbo1);
      while (cursor.hasNext()) {
        DBObject oplogDbo = cursor.next();
        String noteId = oplogDbo.get("dataID").toString();
        DBObject oldNote = coll1.findOne(new BasicDBObject("_id", new ObjectId(noteId)));

        JSONObject noteJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(oldNote));

        DBObject queryDbo2 = (DBObject) new BasicDBObject();
        queryDbo2.put("nodeID", noteId);
        queryDbo2.put("nodeType", 1);
        DBObject shareObject = coll2.findOne(queryDbo2);
        JSONObject shareJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(shareObject));

        Map<String, Object> newNoteMap = DBTransformUtils.transferNoteObject(noteJson, shareJson);
        // List<Map<String, Object>> tagList = getTagListByNoteID(noteId);
        List<String> tagList = getTagIDListByNoteID(noteId);
        if (tagList.size() > 0)
          newNoteMap.put("tagList", tagList);
        List<String> resourceList = getResourceListByNoteID(noteId);
        if (resourceList.size() > 0)
          newNoteMap.put("resourceList", resourceList);
        // mapList.add(newNoteMap);
        coll3.findAndRemove(new BasicDBObject("_id", new ObjectId(noteId)));
        queryExecutor.insertData(newDb, "ln_note", newNoteMap);
        count++;
      }
      logger.info("Success Insert Note count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public void updateTagV2(Long startTime) {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_tag");
      DBCollection coll2 = newDb.getCollection("ln_tag");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("createTime", new BasicDBObject("$gt", startTime));
      cursor = coll1.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject tagJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newTagMap = DBTransformUtils.transferTagObject(tagJson);
        coll2.findAndRemove(new BasicDBObject("_id", new ObjectId(tagJson.getString("_id"))));
        queryExecutor.insertData(newDb, "ln_tag", newTagMap);
        count++;
      }
      logger.info("Success Insert new Tag count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public void updateResourceV2(Long startTime) {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll = db.getCollection("ln_resource");
      DBCollection coll2 = newDb.getCollection("ln_resource");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("genTime", new BasicDBObject("$gt", startTime));
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject resourceJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newResourceMap = DBTransformUtils.transferResourceObject(resourceJson);
        coll2.findAndRemove(new BasicDBObject("_id", new ObjectId(resourceJson.getString("_id"))));
        queryExecutor.insertData(newDb, "ln_resource", newResourceMap);
        count++;
      }
      logger.info("Success Insert new Resource count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public void statisticsIncrementOplogData(Long startTime) {
    int updateOplogDataCount = 0;
    File dataList = null;
    BufferedWriter output = null;
    try {
      DBCollection coll = db.getCollection("ln_oplog");
      DBObject match = new BasicDBObject("$match", new BasicDBObject("sysTime", new BasicDBObject("$gt", startTime)));

      DBObject groupFields = new BasicDBObject("_id", "$dataID");
      groupFields.put("total", new BasicDBObject("$sum", 1));
      DBObject group = new BasicDBObject("$group", groupFields);

      AggregationOutput aggrOutput = coll.aggregate(match, group);
      Iterable<DBObject> dbObjects = aggrOutput.results();
      Iterator<DBObject> dbIter = dbObjects.iterator();
      while (dbIter.hasNext()) {
        DBObject result = dbIter.next();
        String dataID = (String) result.get("_id");
        if (updateOplogDataCount % defaultLimitNum == 0) {
          int round = updateOplogDataCount / defaultLimitNum;
          if (output != null) {
            output.flush();
            output.close();
          }
          dataList = new File(incrementOplogDataPath + File.separator + "oplogData_" + round + ".txt");
          output = new BufferedWriter(new FileWriter(dataList));
        }
        output.write(dataID + "\r\n");
        updateOplogDataCount++;
      }
      logger.info("Success Increment Oplog Data count:" + updateOplogDataCount);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (output != null)
          output.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public List<String> getTagIDListByNoteID(String noteID) {
    List<String> mapList = new ArrayList<String>();
    DBCursor cursor = null;
    try {
      DBCollection coll = db.getCollection("ln_tagnoterelation");
      // DBCollection coll2 = db.getCollection("ln_tag");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("noteID", noteID);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject tagRelationJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        String tagId = tagRelationJson.getString("tagID");
        if (tagId != null) {
          mapList.add(tagId);
        }
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  public List<String> getResourceListByNoteID(String noteID) {
    List<String> mapList = new ArrayList<String>();
    DBCursor cursor = null;
    try {
      DBCollection coll = db.getCollection("ln_resource");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("noteID", noteID);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject resourceJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        String id = resourceJson.getString("genID");
        mapList.add(id);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  private List<Map<String, Object>> createDefaultCategoryOpLog(Long startTime) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll = newDb.getCollection("ln_category");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("categoryType", 0);
      queryDbo1.put("lastUpdateTime", new BasicDBObject("$gt", startTime));
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject categoryJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newOpLogMap = DBTransformUtils.transferDefaultCategoryOpLog(categoryJson);
        // mapList.add(newOpLogMap);
        queryExecutor.insertData(newDb, "ln_oplog", newOpLogMap);
        count++;
      }
      logger.info("Success Insert new default category OpLog count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  private List<Map<String, Object>> createResourceOpLog(Long startTime) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll = newDb.getCollection("ln_resource");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("lastUpdateTime", new BasicDBObject("$gt", startTime));
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject resourceJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newOpLogMap = DBTransformUtils.transferNewResourceOpLog(resourceJson);
        // mapList.add(newOpLogMap);
        queryExecutor.insertData(newDb, "ln_oplog", newOpLogMap);
        count++;
      }
      logger.info("Success Insert new resource OpLog count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  public void incrementUpgradeDBByTime(Long startTime) {
    try {
      checkReloadUser(startTime);

      updateCategoryV2(startTime);

      updateCategoryV22(startTime);

      updateTagV2(startTime);

      updateResourceV2(startTime);

      updateInsertNoteV2(startTime);

      statisticsIncrementOplogData(startTime);

      File dirFile = new File(incrementOplogDataPath);
      File[] files = dirFile.listFiles();
      int fileSize = files.length;
      for (int i = 0; i < fileSize; i++) {
        pool.execute(new OplogThread(files[i], startTime));
      }
      createDefaultCategoryOpLog(startTime);
      createResourceOpLog(startTime);
    } catch (Exception e) {
      logger.error("Increment upgrade failure because of error:" + e);
    }
  }

  public void incrementGreenTeaUpgradeDB() {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("gt_cate");
      DBCollection coll2 = newDb.getCollection("gt_cate");

      cursor = coll1.find();
      while (cursor.hasNext()) {
        DBObject oldDbo = cursor.next();
        String userID = oldDbo.get("userID").toString();

        DBObject newDbo = coll2.findOne(new BasicDBObject("userID", userID));
        if (newDbo == null) {
          coll2.insert(oldDbo, WriteConcern.SAFE);
        }
        count++;
      }
      logger.info("Success Insert new gt_cate count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    
    int count2 = 0;
    DBCursor cursor2 = null;
    try{
      DBCollection coll3 = db.getCollection("gt_data");
      DBCollection coll4 = newDb.getCollection("gt_data");

      cursor2 = coll3.find();
      while (cursor2.hasNext()) {
        DBObject oldDbo = cursor2.next();
        String userID = oldDbo.get("userID").toString();

        DBObject newDbo = coll4.findOne(new BasicDBObject("userID", userID));
        if (newDbo == null) {
          coll4.insert(oldDbo, WriteConcern.SAFE);
        }
        count2++;
      }
      logger.info("Success Insert new gt_data count:" + count2);
    } finally {
      if (cursor2 != null)
        cursor2.close();
    }
  }

  class OplogThread implements Runnable {

    private File dataList = null;
    private Long startTime = null;

    public OplogThread(File dataList, Long startTime) {
      this.dataList = dataList;
      this.startTime = startTime;
    }

    @Override
    public void run() {
      // TODO Auto-generated method stub
      updateOplogV2ByFile();
    }

    public void updateOplogV2ByDataID(Long startTime, String dataID) {
      Map<String, Object> dataMap = new HashMap<String, Object>();
      DBCursor cursor = null;
      try {
        DBCollection coll0 = newDb.getCollection("ln_oplog");
        DBObject queryDbo0 = (DBObject) new BasicDBObject();
        queryDbo0.put("dataID", dataID);
        DBObject newDbo = coll0.findOne(queryDbo0);
        if (newDbo != null) {
          dataMap.put("dataID", newDbo.get("dataID"));
          dataMap.put("userID", newDbo.get("userID"));
          dataMap.put("dataType", newDbo.get("dataType"));
          dataMap.put("isAvailible", newDbo.get("isAvailible"));
          dataMap.put("createTimeStamp", newDbo.get("createTimeStamp"));
          dataMap.put("timeStamp", newDbo.get("timeStamp"));
          dataMap.put("createTime", newDbo.get("createTime"));
          dataMap.put("lastUpdateTime", newDbo.get("lastUpdateTime"));
        }

        DBCollection coll = db.getCollection("ln_oplog");
        DBObject queryDbo1 = (DBObject) new BasicDBObject();
        queryDbo1.put("dataID", dataID);
        queryDbo1.put("sysTime", new BasicDBObject("$gt", startTime));
        cursor = coll.find(queryDbo1);
        while (cursor.hasNext()) {
          DBObject opDbo = cursor.next();
          dataMap = DBTransformUtils.transferOpLogObject(opDbo, dataMap);
        }
      } finally {
        if (cursor != null)
          cursor.close();
      }
      DBCollection coll2 = newDb.getCollection("ln_oplog");
      coll2.findAndRemove(new BasicDBObject("dataID", dataID));
      queryExecutor.insertData(newDb, "ln_oplog", dataMap);
      logger.debug("Success Insert/Update Oplog dataID:" + dataID);
    }

    public void updateOplogV2ByFile() {
      if (dataList != null) {
        logger.info("Start Update Oplog:" + Thread.currentThread().getName());
        int count = 0;
        BufferedReader input = null;
        long currentTime = System.currentTimeMillis();
        long executeTime = 0L;
        try {
          input = new BufferedReader(new FileReader(dataList));
          String str = null;
          while ((str = input.readLine()) != null) {
            try {
              updateOplogV2ByDataID(startTime, str);
              count++;
            } catch (Exception e) {
              logger.error("Oplog upgrade failure dataID:" + str + ",because of " + e);
            }
          }
          long currentTime2 = System.currentTimeMillis();
          executeTime = currentTime2 - currentTime;
          executeOplogCount.addAndGet(count);
          logger.info("Success Insert/update OpLog total count:" + executeOplogCount);
        } catch (IOException e) {
          logger.error("Oplog upgrade failure (File " + dataList.getAbsolutePath() + ") because of error:" + e);
        } finally {
          try {
            if (input != null) {
              input.close();
            }
          } catch (IOException e) {
            logger.error("Closing the open file failure (File " + dataList.getAbsolutePath() + ") because of error:" + e);
          }
          String notification =
              Thread.currentThread().getName() + ",Oplog dataID from " + dataList.getAbsolutePath() + ",Total import oplog count:"
                  + executeOplogCount.get() + ",ExecuteTime=" + executeTime / 1000 + "s";
          logger.info(notification);
        }
      }
    }
  }

}
