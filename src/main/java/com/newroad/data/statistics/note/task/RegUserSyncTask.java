package com.newroad.data.statistics.note.task;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.statistics.datamodel.RegisterUserRecord;
import com.newroad.data.statistics.exception.DataExceptionHandler;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.common.db.mysql.MySqlDao;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RegUserSyncTask implements Callable<Integer> {

  private static final Logger logger = LoggerFactory.getLogger(RegUserSyncTask.class);

  private DB db;

  private Long startStatisticsTime = 0L;
  private Long endStatisticsTime = 0L;
  private Integer startIndex;
  private Integer limit;

  private CountDownLatch threadSignal;
  private Integer round;
  private MySqlDao mysqlDao;

  public RegUserSyncTask(DB db, MySqlDao mysqlDao) {
    this.db = db;
    this.mysqlDao = mysqlDao;
  }

  public RegUserSyncTask(CountDownLatch threadSignal, Integer round, DB db, Long startStatisticsTime, Long endStatisticsTime,
      Integer startIndex, Integer limit, MySqlDao mysqlDao) {
    this.threadSignal = threadSignal;
    this.round = round;

    this.db = db;
    // logger.info("The statistics start time:" + startStatisticsTime + ",end time:" +
    // endStatisticsTime);
    this.startStatisticsTime = startStatisticsTime;
    this.endStatisticsTime = endStatisticsTime;
    this.startIndex = startIndex;
    this.limit = limit;

    this.mysqlDao = mysqlDao;
  }


  @Override
  public Integer call() throws Exception {
    logger.info("RegUserSyncTask Thread " + Thread.currentThread() + " started, execute round:" + round);
    try {
      registerUserStatistics(startIndex, limit);
    } catch (Exception e) {
      logger.error("RegUserSyncTask Thread " + Thread.currentThread() + " interrupted, execute round:" + round + ",error:" + e);
    } finally {
      threadSignal.countDown();
      logger.info("RegUserSyncTask Thread " + Thread.currentThread() + " completed, remain count:" + threadSignal.getCount() + ", execute round:" + round);
    }
    return 1;
  }

  /**
   * @info statistics register user by userId
   */
  public void registerUserStatistics(String userId) {
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_USER);
    DBCursor cursor = null;
  
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("_id", new ObjectId(userId));
      DBObject result = coll.findOne(queryDbo1);

      JSONObject userJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(result));
      userId = userJson.getString("_id");
      Long createTime = userJson.getLong("createTime");
      Long lastUpdateTime = userJson.getLong("lastUpdateTime");

      Long userNoteCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_NOTE, userId, endStatisticsTime);
      Long userCategoryCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_CATE, userId, endStatisticsTime);
      Long userTagCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_TAG, userId, endStatisticsTime);
      Long userResourceCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_RESOURCE, userId, endStatisticsTime);
      Integer userSyncCount = UserCountStatisticsToolkit.countSyncStatisticsByUser(userId, endStatisticsTime);
      Long maxUserSyncTime = UserCountStatisticsToolkit.maxSyncTimeByUser(userId, endStatisticsTime);

      RegisterUserRecord registerRecord =
          new RegisterUserRecord(NoteUserStatisticsConstants.UNKNOWN, NoteUserStatisticsConstants.UNKNOWN, "", "", "", userId, "LenovoID",
              new Date(createTime), userNoteCount, userCategoryCount, userTagCount, userResourceCount, 0, userSyncCount, new Date(
                  lastUpdateTime), maxUserSyncTime);

      mysqlDao.insert(NoteUserStatisticsConstants.INSERT_REGISTER_USER_RECORD, registerRecord);
    } finally {
      if (cursor != null)
        cursor.close();
    }
    logger.info("insert oms_reguser_statistics data userID:" + userId + ".");
  }

  /**
   * @info statistics register user by ln_user_statistics records
   */
  public void registerUserStatistics(int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_USER);
    DBCollection coll2 = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    coll2.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    DBCursor cursor = null;

    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

      while (cursor.hasNext()) {
        JSONObject userJson = null;
        String userId = null;
        try {
          userJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          userId = userJson.getString("_id");
          Long createTime = userJson.getLong("createTime");
          Long lastUpdateTime = userJson.getLong("lastUpdateTime");

          Long userNoteCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_NOTE, userId, endStatisticsTime);
          Long userCategoryCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_CATE, userId, endStatisticsTime);
          Long userTagCount = UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_TAG, userId, endStatisticsTime);
          Long userResourceCount =
              UserCountStatisticsToolkit.countNoteStatisticsByUser(NoteUserStatisticsConstants.COLLECTION_RESOURCE, userId, endStatisticsTime);
          Integer userSyncCount = UserCountStatisticsToolkit.countSyncStatisticsByUser(userId, endStatisticsTime);
          Long maxUserSyncTime = UserCountStatisticsToolkit.maxSyncTimeByUser(userId, endStatisticsTime);

          RegisterUserRecord registerRecord = null;

           DBObject queryDbo2 = (DBObject) new BasicDBObject();
           queryDbo2.put("operateType", OperateType.register.toString());
           queryDbo2.put("userID", userId);
           DBObject statisticsResult = coll2.findOne(queryDbo2);
           if (statisticsResult != null) {
           JSONObject statisticsJson = JSONObject.fromObject(statisticsResult);
           registerRecord =
           new RegisterUserRecord(statisticsJson, userId, createTime, userNoteCount,
           userCategoryCount, userTagCount, userResourceCount,
           userSyncCount, maxUserSyncTime);
           } else {
          registerRecord =
              new RegisterUserRecord(NoteUserStatisticsConstants.UNKNOWN, NoteUserStatisticsConstants.UNKNOWN, "", "", "", userId, "LenovoID",
                  new Date(createTime), userNoteCount, userCategoryCount, userTagCount, userResourceCount, 0, userSyncCount, new Date(
                      lastUpdateTime), maxUserSyncTime);
          }
          mysqlDao.insert(NoteUserStatisticsConstants.INSERT_REGISTER_USER_RECORD, registerRecord);
          count++;
        } catch (Exception e) {
          logger.error("insert oms_reguser_statistics userId=" + userId + "&JSONObject=" + userJson + " data failure:" + e);
          DataExceptionHandler.writeFile(userId);
        }
      }
    } finally {
      if (cursor != null){
        cursor.close();
      }  
    }
    logger.info("insert oms_reguser_statistics data startIndex:" + startIndex + " & EndIndex:" + (startIndex + count) + ".");
  }
}
