package com.newroad.data.statistics.note.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.statistics.datamodel.DeviceRecord;
import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.statistics.datamodel.RegisterUserRecord;
import com.newroad.data.statistics.datamodel.UserActiveStatistics;
import com.newroad.data.statistics.datamodel.UserLoginStatistics;
import com.newroad.data.statistics.datamodel.UserSyncStatistics;
import com.newroad.data.statistics.note.action.UserStatisticsAnalyzer;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.common.db.mysql.MySqlDao;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class UserOpLogStatisticsTask implements Callable<Integer> {

  private static final Logger logger = LoggerFactory.getLogger(UserOpLogStatisticsTask.class);

  private DB db;

  private OperateType operateType;
  private Long startStatisticsTime = 0L;
  private Long endStatisticsTime = 0L;
  private Integer startIndex;
  private Integer limit;

  private CountDownLatch threadSignal;
  private Integer round;
  private MySqlDao mysqlDao;

  public UserOpLogStatisticsTask(CountDownLatch threadSignal, Integer round, DB db, OperateType operateType, Long startStatisticsTime,
      Long endStatisticsTime, Integer startIndex, Integer limit, MySqlDao mysqlDao) {
    this.threadSignal = threadSignal;
    this.round = round;

    this.db = db;
    this.operateType = operateType;
    // logger.info("The statistics start time:" + startStatisticsTime + ",end time:" +
    // endStatisticsTime);
    this.startStatisticsTime = startStatisticsTime;
    this.endStatisticsTime = endStatisticsTime;
    this.startIndex = startIndex;
    this.limit = limit;

    this.mysqlDao = mysqlDao;
  }

  public Integer call() {
    return loadStatistics(operateType, startIndex, limit);
  }

  public Integer loadStatistics(OperateType operateType, int startIndex, int limit) {
    logger.info("UserStatisticsLoadTask Thread " + Thread.currentThread() + " started, execute round:" + round + ", operateType:"
        + operateType);
    try {
      switch (operateType) {
        case install:
          deviceStatistics(operateType, startIndex, limit);
          break;
        case activate:
          deviceStatistics(operateType, startIndex, limit);
          break;
        case register:
          registerUserUpdateStatistics(startIndex, limit);
          break;
        case start:
          userActiveStatistics(operateType, startIndex, limit);
          break;
        case stop:
          userActiveStatistics(operateType, startIndex, limit);
          break;
        case login:
          userLoginStatistics(operateType, startIndex, limit);
          break;
        case logout:
          userLoginStatistics(operateType, startIndex, limit);
          break;
        case synchronize:
          userSyncStatistics(operateType, startIndex, limit);
          break;
        case commit:
          userSyncStatistics(operateType, startIndex, limit);
          break;
        default:
          break;
      }
      UserStatisticsAnalyzer.executeThreadCount.addAndGet(1);
      // logger.info("UserStatisticsLoadTask Thread " + Thread.currentThread() +
      // " completed, execute round:" + round + ", operateType:" + operateType);
    } catch (Exception e) {
      logger.error("UserStatisticsLoadTask Thread " + Thread.currentThread() + " interrupted, execute round:" + round + ", operateType:"
          + operateType + ",error:" + e);
    } finally {
      threadSignal.countDown();
      logger.info("UserStatisticsLoadTask Thread " + Thread.currentThread() + "completed, remain count:" + threadSignal.getCount() + ", execute round:"
          + round + ", operateType:" + operateType);
    }
    return 1;
  }

  public void deviceStatistics(OperateType operateType, int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operateType", operateType.toString());
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      while (cursor.hasNext()) {
        JSONObject statisticsJson = null;
        try {
          statisticsJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          DeviceRecord deviceRecord = new DeviceRecord(statisticsJson);
          mysqlDao.insert(NoteUserStatisticsConstants.INSERT_DEVICE_RECORD, deviceRecord);
        } catch (Exception e) {
          logger.error("insert oms_device_statistics data info " + statisticsJson + " failure:" + e);
        } finally {
          count++;
        }
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    logger.info("insert oms_device_statistics data count " + count + " when operateType is " + operateType + " status.");
  }

  public void registerUserUpdateStatistics(int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operateType", OperateType.register.toString());
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      
      while (cursor.hasNext()) {
        JSONObject statisticsJson = null;
        try {
          statisticsJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          RegisterUserRecord registerRecord = new RegisterUserRecord(statisticsJson);
          mysqlDao.update(NoteUserStatisticsConstants.UPDATE_REGUSER_STATISTICS, registerRecord);
        } catch (Exception e) {
          logger.error("update oms_reguser_statistics data info " + statisticsJson + " failure:" + e);
        } finally {
          count++;
        }
      }
    } finally {
      if (cursor != null){
        cursor.close();
      }
    }
    logger.info("update oms_reguser_statistics data startIndex:" + startIndex + " & EndIndex:" + (startIndex + count) + ".");
  }

  public void userActiveStatistics(OperateType operateType, int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operateType", operateType.toString());
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      
      while (cursor.hasNext()) {
        JSONObject statisticsJson = null;
        try {
          statisticsJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          UserActiveStatistics activeRecord = new UserActiveStatistics(statisticsJson);
          mysqlDao.insert(NoteUserStatisticsConstants.INSERT_USER_ACTIVE_RECORD, activeRecord);
        } catch (Exception e) {
          logger.error("insert oms_useractive_statistics data info " + statisticsJson + " failure:" + e);
        } finally {
          count++;
        }
      }
    } finally {
      if (cursor != null){
        cursor.close();
      }  
    }
    logger.info("insert oms_useractive_statistics data count " + count + " when operateType is " + operateType + " status.");
  }

  public void userLoginStatistics(OperateType operateType, int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operateType", operateType.toString());
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

      while (cursor.hasNext()) {
        JSONObject statisticsJson = null;
        try {
          statisticsJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          UserLoginStatistics loginRecord = new UserLoginStatistics(statisticsJson);
          mysqlDao.insert(NoteUserStatisticsConstants.INSERT_USER_LOGIN_STATISTICS, loginRecord);
        } catch (Exception e) {
          logger.error("insert oms_userlogin_statistics data info " + statisticsJson + " failure:" + e);
        } finally {
          count++;
        }
      }
    } finally {
      if (cursor != null){
        cursor.close();
      }  
    }
    logger.info("insert oms_userlogin_statistics data count " + count + " when operateType is " + operateType + " status.");
  }

  public void userSyncStatistics(OperateType operateType, int startIndex, int limit) {
    int count = 0;
    DBCollection coll = db.getCollection(NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    DBCursor cursor = null;
    try {
      DBObject queryDbo1 = (DBObject) new BasicDBObject();
      queryDbo1.put("operateType", operateType.toString());
      DBObject statisticsTimeDbo = (DBObject) new BasicDBObject();
      statisticsTimeDbo.put("$gte", startStatisticsTime);
      statisticsTimeDbo.put("$lt", endStatisticsTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      cursor = coll.find(queryDbo1).skip(startIndex).limit(limit);
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      
      while (cursor.hasNext()) {
        JSONObject statisticsJson = null;
        try {
          statisticsJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
          UserSyncStatistics syncRecord = new UserSyncStatistics(statisticsJson);
          mysqlDao.insert(NoteUserStatisticsConstants.INSERT_USER_SYNC_STATISTICS, syncRecord);
        } catch (Exception e) {
          logger.error("insert oms_usersync_statistics data info " + statisticsJson + " failure:" + e);
        } finally {
          count++;
        }
      }
    } finally {
      if (cursor != null){
        cursor.close();
      }  
    }
    logger.info("insert oms_usersync_statistics data count " + count + " when operateType is " + operateType + " status.");
  }

}
