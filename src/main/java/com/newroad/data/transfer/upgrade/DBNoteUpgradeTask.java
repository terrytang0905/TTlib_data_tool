package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.db.mongo.MongoQueryExecutor;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.common.listener.NotificationThread;
import com.newroad.data.common.listener.TaskListener;
import com.newroad.data.transfer.utils.DBTransformUtils;
import com.newroad.data.transfer.utils.TagNoteCount;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 7, 2014
 */
public class DBNoteUpgradeTask extends NotificationThread {

  private static Logger logger = LoggerFactory.getLogger(DBNoteUpgradeTask.class);

  private static AtomicInteger executeUserCount = new AtomicInteger(0);

  private int round;
  private DB db;
  private DB newDb;
  private MongoQueryExecutor queryExecutor;
  private int startIndex;
  private int limit;
  private File userFile;


  private String notification;

  /**
   * @param db
   * @param newDb
   */
  public DBNoteUpgradeTask(int round, DB db, DB newDb, int startIndex, int limit) {
    super();
    this.round = round;
    this.db = db;
    this.newDb = newDb;
    this.queryExecutor = new MongoQueryExecutor();
    this.startIndex = startIndex;
    this.limit = limit;
  }

  /**
   * @param db
   * @param newDb
   */
  public DBNoteUpgradeTask(int round, DB db, DB newDb, File userFile) {
    super();
    this.round = round;
    this.db = db;
    this.newDb = newDb;
    this.queryExecutor = new MongoQueryExecutor();
    this.userFile = userFile;
  }


  @Override
  public void doWork() {
    upgradeDBByFileUserID();
    // upgradeDBByIndex();
  }

  public void doWorkByUserID(String userID) {
    upgradeDBBySingleUserID(userID);
  }

  public String getNotification() {
    return notification;
  }

  public void setNotification(String notification) {
    this.notification = notification;
  }

  public List<Map<String, Object>> convertCategoryV2(String userID) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_category");
      DBCollection coll2 = db.getCollection("ln_sharelink");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("belong", userID);
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
        mapList.add(newCategoryMap);
        count++;
      }
      logger.debug("Success Insert new Category count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  public void convertInsertNoteV2(String userID) {
    // List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_note");
      DBCollection coll2 = db.getCollection("ln_sharelink");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("belong", userID);
      cursor = coll1.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject noteJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        String noteId = noteJson.getString("_id");

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
        queryExecutor.insertData(newDb, "ln_note", newNoteMap);
        count++;
      }
      logger.debug("Success Insert new Note count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
  }

  public List<Map<String, Object>> convertTagV2(String userID) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll1 = db.getCollection("ln_tag");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("creator", userID);
      cursor = coll1.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject tagJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newTagMap = DBTransformUtils.transferTagObject(tagJson);
        mapList.add(newTagMap);
        count++;
      }
      logger.debug("Success Insert new Tag count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  public List<TagNoteCount> updateTagRelationV2(String userID) {
    List<TagNoteCount> mapList = new ArrayList<TagNoteCount>();
    int count = 0;
    DBCursor cursor1 = null;
    try {
      DBCollection coll1 = newDb.getCollection("ln_tag");
      DBCollection coll2 = db.getCollection("ln_tagnoterelation");
      cursor1 = coll1.find();
      while (cursor1.hasNext()) {
        String tagID = ((ObjectId) cursor1.next().get("_id")).toString();
        if (tagID != null) {
          DBObject queryDbo2 = (DBObject) new BasicDBObject();
          queryDbo2.put("belong", userID);
          queryDbo2.put("tagID", tagID);
          long noteTagCount = coll2.count(queryDbo2);
          TagNoteCount tagCount = new TagNoteCount(tagID, Long.valueOf(noteTagCount).intValue());
          mapList.add(tagCount);
        }
      }
      logger.debug("Success update new Tag count:" + count);
    } finally {
      if (cursor1 != null)
        cursor1.close();
    }
    return mapList;
  }


  public List<Map<String, Object>> convertResourceV2(String userID) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll = db.getCollection("ln_resource");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("belong", userID);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject resourceJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newResourceMap = DBTransformUtils.transferResourceObject(resourceJson);
        mapList.add(newResourceMap);
        count++;
      }
      logger.debug("Success Insert new Resource count:" + count);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  public List<Map<String, Object>> convertOplogV2(String userID) {
    Map<String, Map<String, Object>> mapList = new HashMap<String, Map<String, Object>>();
    DBCursor cursor = null;
    try {
      DBCollection coll = db.getCollection("ln_oplog");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operator", userID);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        DBObject dbObj = cursor.next();
        String dataID = dbObj.get("dataID").toString();
        Map<String, Object> perviouslogMap = null;
        if (mapList.containsKey(dataID)) {
          perviouslogMap = mapList.get(dataID);
        }
        mapList.put(dataID, DBTransformUtils.transferOpLogObject(dbObj, perviouslogMap));
      }
      List<Map<String, Object>> filterList = new ArrayList<Map<String, Object>>(mapList.values());
      logger.debug("Success Insert new OpLog count:" + mapList.size());
      return filterList;
    } finally {
      if (cursor != null)
        cursor.close();
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
        String tagID = cursor.next().get("tagID").toString();
        if (tagID != null) {
          mapList.add(tagID);
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
        String clientResourceID = cursor.next().get("genID").toString();
        if (clientResourceID != null) {
          mapList.add(clientResourceID);
        }
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  private List<Map<String, Object>> createDefaultCategoryOpLog(String userID) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    DBCursor cursor = null;
    try {
      DBCollection coll = newDb.getCollection("ln_category");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("userID", userID);
      queryDbo1.put("categoryType", 0);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject categoryJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newOpLogMap = DBTransformUtils.transferDefaultCategoryOpLog(categoryJson);
        mapList.add(newOpLogMap);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }


  private List<Map<String, Object>> createResourceOpLog(String userID) {
    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    DBCursor cursor = null;
    try {
      DBCollection coll = newDb.getCollection("ln_resource");
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("userID", userID);
      cursor = coll.find(queryDbo1);
      while (cursor.hasNext()) {
        JSONObject resourceJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        Map<String, Object> newOpLogMap = DBTransformUtils.transferNewResourceOpLog(resourceJson);
        mapList.add(newOpLogMap);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return mapList;
  }

  // private void attachNoteTagResource(String userID) {
  // int count = 0;
  // DBCursor cursor2 = null;
  // try {
  // DBCollection coll2 = newDb.getCollection("ln_note");
  // DBObject queryDbo = (DBObject) new BasicDBObject();
  // queryDbo.put("userID", userID);
  // cursor2 = coll2.find(queryDbo);
  // // List<JSONObject> list = new ArrayList<JSONObject>();
  // while (cursor2.hasNext()) {
  // DBObject noteObj = cursor2.next();
  // ObjectId noteId = (ObjectId) noteObj.get("_id");
  //
  // DBObject queryDbo2 = (DBObject) new BasicDBObject();
  // queryDbo2.put("_id", noteId);
  // DBObject updateDbo2 = (DBObject) new BasicDBObject();
  // if (noteId == null) {
  // System.out.println("Attachment NoteID is null!");
  // continue;
  // }
  // List<Map<String, Object>> tagList = getTagListByNoteID(noteId.toString());
  // if (tagList.size() > 0) {
  // updateDbo2.put("tagList", tagList);
  // }
  // List<String> resourceList = getResourceListByNoteID(noteId.toString());
  // if (resourceList.size() > 0) {
  // updateDbo2.put("resourceList", resourceList);
  // }
  // if (updateDbo2.keySet().size() > 0) {
  // DBObject updateSet = (DBObject) new BasicDBObject();
  // updateSet.put("$set", updateDbo2);
  // coll2.update(queryDbo2, updateSet, false, true);
  // count++;
  // }
  // }
  // logger.debug("Success Update new Tag&Resource Note count:" + count);
  // } finally {
  // if (cursor2 != null)
  // cursor2.close();
  // }
  // }

  public void upgradeDBBySingleUserID(String userID) {
    String[] collectionArray=MongoConnectionFactory.collectionArray;
    for (String collection : collectionArray) {
      removeCollectionByUser(collection, userID);
    }
    removeCollectionByUser("ln_oplog", userID);
    
    DBCollection coll = db.getCollection("ln_user");
    DBCollection coll2 = db.getCollection("ln_oplog_counter");
    DBCursor cursor = null;
    cursor = coll.find(new BasicDBObject("_id", new ObjectId("5247d489c8f224ead04c9911")));
    while (cursor.hasNext()) {
      JSONObject userJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("userID", userID);
      DBObject oplogcounter = MongoDBUtilizer.translateId2String(coll2.findOne(queryDbo1));
      JSONObject oplogJson = JSONObject.fromObject(oplogcounter);
      Map<String, Object> map = DBTransformUtils.transferUserObject(userJson, oplogJson);
      // mapList.add(map);
      queryExecutor.insertData(newDb, "ln_user", map);
    }
    upgradeDBByUser(userID);
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

  public void upgradeDBByFileUserID() {
    if (userFile == null) {
      return;
    }
    int count = 0;
    BufferedReader input = null;
    long startTime = System.currentTimeMillis();
    try {
      input = new BufferedReader(new FileReader(userFile));
      String str = null;
      while ((str = input.readLine()) != null) {
        try {
          upgradeDBByUser(str);
          count++;
        } catch (Exception e) {
          logger.error("upgrade failure userID:" + str + ",because of " + e);
        }
      }
    } catch (IOException e) {
      logger.error("upgrade failure (File " + userFile.getAbsolutePath() + ") because of error:" + e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
        logger.error("Closing the open file failure (File " + userFile.getAbsolutePath() + ") because of error:" + e);
      }
      executeUserCount.addAndGet(count);
      long endTime = System.currentTimeMillis();
      String notification =
          "Round " + round + ":" + Thread.currentThread().getName() + ",Import User Data from " + userFile.getAbsolutePath()
              + ",Total import user count:" + executeUserCount.get() + ",ExecuteTime=" + (endTime - startTime) / 1000 + "s";
      logger.info(notification);
    }
  }

  public int upgradeDBByIndex() {
    int count = 0;
    DBCursor cursor = null;
    try {
      DBCollection coll = newDb.getCollection("ln_user");
      cursor = coll.find().skip(startIndex).limit(limit);
      while (cursor.hasNext()) {
        String userId = ((ObjectId) cursor.next().get("_id")).toString();
        try {
          upgradeDBByUser(userId);
          count++;
        } catch (Exception e) {
          logger.error("upgrade failure userID:" + userId + ",because of " + e);
        }
      }
    } catch (Exception e) {
      logger.error("upgrade failure (from " + startIndex + " limit " + limit + ") because of error:" + e);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return count;
  }

  public void upgradeDBByUser(String id) {
    // long start = System.currentTimeMillis();
    List<Map<String, Object>> cateMapList = convertCategoryV2(id);
    queryExecutor.batchInsertData(newDb, "ln_category", cateMapList);
    // long end = System.currentTimeMillis() - start;
    // logger.info("UserID:" + id + " Category upgrade execute time:" + end);

    // start = System.currentTimeMillis();
    List<Map<String, Object>> tagMapList = convertTagV2(id);
    queryExecutor.batchInsertData(newDb, "ln_tag", tagMapList);
    // List<TagNoteCount> tagRelateList = updateTagRelationV2(id);
    // for (TagNoteCount tagCount : tagRelateList) {
    // Map<String, Object> queryMap = new HashMap<String, Object>(1);
    // queryMap.put("_id", new ObjectId(tagCount.getTagID()));
    // Map<String, Object> updateMap = new HashMap<String, Object>(1);
    // updateMap.put("tagNoteCount", tagCount.getTagNoteCount());
    // queryExecutor.updateData(newDb, "ln_tag", queryMap, updateMap);
    // }

    // end = System.currentTimeMillis() - start;
    // logger.info("UserID:" + id + " Tag upgrade execute time:" + end);

    // start = System.currentTimeMillis();
    List<Map<String, Object>> resourceMapList = convertResourceV2(id);
    queryExecutor.batchInsertData(newDb, "ln_resource", resourceMapList);
    // end = System.currentTimeMillis() - start;
    // logger.info("UserID:" + id + " Resource upgrade execute time:" + end);

    // start = System.currentTimeMillis();
    convertInsertNoteV2(id);
    // end = System.currentTimeMillis() - start;
    // logger.info("UserID:" + id + " Note upgrade execute time:" + end);

    // start = System.currentTimeMillis();
    List<Map<String, Object>> oplogMapList = convertOplogV2(id);
    queryExecutor.batchInsertData(newDb, "ln_oplog", oplogMapList);
    List<Map<String, Object>> defaultCategoryOplogList = createDefaultCategoryOpLog(id);
    queryExecutor.batchInsertData(newDb, "ln_oplog", defaultCategoryOplogList);
    List<Map<String, Object>> resourceOplogList = createResourceOpLog(id);
    queryExecutor.batchInsertData(newDb, "ln_oplog", resourceOplogList);
    // end = System.currentTimeMillis() - start;
    // logger.info("UserID:" + id + " Oplog upgrade execute time:" + end);
  }

  class DBTransferListener implements TaskListener {
    @Override
    public void threadComplete(Runnable runner) {
      // TODO Auto-generated method stub
      logger.info("Round " + round + ":" + Thread.currentThread().getName() + " is completed!");
    }
  }
}
