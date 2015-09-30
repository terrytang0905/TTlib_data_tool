package com.newroad.data.statistics.note.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;

public class UserCountStatisticsToolkit {

  private static final Logger logger = LoggerFactory.getLogger(UserCountStatisticsToolkit.class);

  public static Long countRegUserStatistics(Long startTime, Long endTime) {
    Long userCount = null;
    DBCollection coll =
        MongoConnectionFactory.getDefaultDB()
            .getCollection(NoteUserStatisticsConstants.COLLECTION_USER);
    try {
      DBObject queryDbo1 = (DBObject)new BasicDBObject();
      DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
      statisticsTimeDbo.put("$gte", startTime);
      statisticsTimeDbo.put("$lt", endTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      userCount = coll.count(queryDbo1);
      logger.info("Register user statistics count:" + userCount);
    } catch (MongoException me) {
      logger.error("countRegUserStatistics MongoException:", me);
    }
    return userCount;
  }

  public static Long countOperateStatistics(OperateType operateType, Long startTime, Long endTime) {
    Long operateCount = null;
    DBCollection coll =
        MongoConnectionFactory.getDefaultDB().getCollection(
            NoteUserStatisticsConstants.COLLECTION_STATISTICS);
    coll.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    try {
      DBObject queryDbo1 = (DBObject)new BasicDBObject();
      queryDbo1.put("operateType", operateType.toString());
      DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
      statisticsTimeDbo.put("$gte", startTime);
      statisticsTimeDbo.put("$lt", endTime);
      queryDbo1.put("createTime", statisticsTimeDbo);
      operateCount = coll.count(queryDbo1);
      logger.info("User operate " + operateType + " statistics count:" + operateCount);
    } catch (MongoException me) {
      logger.error("countOperateStatistics MongoException:", me);
    }
    return operateCount;
  }

  public static Integer countUserSyncStatistics(Long statisticsEndTime) {
    Integer count = null;

    DBCollection coll =
        MongoConnectionFactory.getDefaultDB().getCollection(
            NoteUserStatisticsConstants.COLLECTION_OPLOG);

    List<DBObject> pipeline = new ArrayList<DBObject>();

    DBObject cond = new BasicDBObject();
    DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
    statisticsTimeDbo.put("$lt", statisticsEndTime);
    cond.put("lastUpdateTime", statisticsTimeDbo);
    DBObject match = new BasicDBObject("$match", cond);
    pipeline.add(match);

    DBObject groupCond = new BasicDBObject();
    groupCond.put("_id", "$userID");
    groupCond.put("total", new BasicDBObject("$sum", 1));
    DBObject group = new BasicDBObject("$group", groupCond);
    pipeline.add(group);

    DBObject tmpCond = new BasicDBObject();
    tmpCond.put("_id", "$_id");
    tmpCond.put("total", "$total");
    DBObject projectCond = new BasicDBObject("tmp", tmpCond);
    DBObject project = new BasicDBObject("$project", projectCond);
    pipeline.add(project);

    DBObject groupCond2 = new BasicDBObject();
    groupCond2.put("_id", null);
    groupCond2.put("count", new BasicDBObject("$sum", 1));
    DBObject group2 = new BasicDBObject("$group", groupCond2);
    pipeline.add(group2);

    AggregationOutput aggregate = coll.aggregate(pipeline);
    Iterable<DBObject> iterable = aggregate.results();
    Iterator<DBObject> iter = iterable.iterator();
    while (iter.hasNext()) {
      DBObject result = iter.next();
      count = (Integer)result.get("count");
    }
//    CommandResult commandResult = aggregate.getCommandResult();
//    BasicDBList list = (BasicDBList)commandResult.get("result");
//    if (list.size() > 0) {
//      DBObject result = (DBObject)list.get(0);
//      count = (Integer)result.get("count");
//    }
    logger.info("Sync User overall statistics count:" + count);
    return count;
  }

  public static Long countNoteStatisticsByUser(String collection, String userId,
      Long statisticsEndTime) {
    Long count = null;
    DBCollection coll2 = MongoConnectionFactory.getDefaultDB().getCollection(collection);
    DBObject queryDbo2 = new BasicDBObject();
    if (userId != null) {
      queryDbo2.put("userID", userId);
    }
    queryDbo2.put("status", 1);
    DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
    statisticsTimeDbo.put("$lt", statisticsEndTime);
    queryDbo2.put("lastUpdateTime", statisticsTimeDbo);
    count = coll2.count(queryDbo2);
    logger.info("User Note info statistics count:" + count);
    return count;
  }

  public static Integer countSyncStatisticsByUser(String userId, Long statisticsEndTime) {
    Integer count = 0;

    DBCollection coll =
        MongoConnectionFactory.getDefaultDB().getCollection(
            NoteUserStatisticsConstants.COLLECTION_OPLOG);

    List<DBObject> pipeline = new ArrayList<DBObject>();

    DBObject cond = new BasicDBObject();
    if (userId != null) {
      cond.put("userID", userId);
    }
    DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
    statisticsTimeDbo.put("$lt", statisticsEndTime);
    cond.put("lastUpdateTime", statisticsTimeDbo);
    DBObject match = new BasicDBObject("$match", cond);
    pipeline.add(match);

    DBObject groupCond = new BasicDBObject();
    groupCond.put("_id", "$userID");
    groupCond.put("total", new BasicDBObject("$sum", 1));
    DBObject group = new BasicDBObject("$group", groupCond);
    pipeline.add(group);

    AggregationOutput aggregate = coll.aggregate(pipeline);
    Iterable<DBObject> iterable = aggregate.results();
    Iterator<DBObject> iter = iterable.iterator();
    while (iter.hasNext()) {
      DBObject result = iter.next();
      count = (Integer)result.get("total");
    }
//    CommandResult commandResult = aggregate.getCommandResult();
//    BasicDBList list = (BasicDBList)commandResult.get("result");
//    if (list.size() > 0) {
//      DBObject result = (DBObject)list.get(0);
//      count = (Integer)result.get("total");
//    }
    logger.info("User Sync Number statistics count:" + count);
    return count;
  }

  public static Long maxSyncTimeByUser(String userId, Long statisticsEndTime) {
    DBCursor cursor = null;
    Long lastUpdateTime = null;
    try {
      DBCollection coll2 =
          MongoConnectionFactory.getDefaultDB().getCollection(
              NoteUserStatisticsConstants.COLLECTION_OPLOG);
      DBObject queryDbo2 = new BasicDBObject();
      queryDbo2.put("userID", userId);
      DBObject statisticsTimeDbo = (DBObject)new BasicDBObject();
      statisticsTimeDbo.put("$lt", statisticsEndTime);
      queryDbo2.put("lastUpdateTime", statisticsTimeDbo);
      DBObject sortDbo = new BasicDBObject("lastUpdateTime", -1);
      cursor = coll2.find(queryDbo2).sort(sortDbo);
      while (cursor.hasNext()) {
        JSONObject syncJson =
            JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        lastUpdateTime = syncJson.getLong("lastUpdateTime");
        break;
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
      logger.info("User " + userId + " maxSyncTime " + lastUpdateTime);
    }
    return lastUpdateTime;
  }

}
