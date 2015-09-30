package com.newroad.data.transfer.upgrade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.db.mongo.MongoQueryExecutor;
import com.newroad.data.common.db.mongo.MongoDBUtilizer;
import com.newroad.data.transfer.utils.DBTransformUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 20, 2014
 */
public class DBUpgradeExecutor {

  private static Logger logger = LoggerFactory.getLogger(DBUpgradeExecutor.class);

  private static ThreadPoolExecutor pool = new ThreadPoolExecutor(150, 1000, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(150),
      new ThreadPoolExecutor.DiscardOldestPolicy());

  private static int defaultLimitNum = 1000;
  private int importUserCount = 0;

  private DB db;
  private DB newDb;
  private MongoQueryExecutor queryExecutor;
  private String localFolder;

  /**
   * @param dbName1
   * @param dbName2
   */
  public DBUpgradeExecutor(String dbName1, String dbName2, String localFolder) {
    super();
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.queryExecutor = new MongoQueryExecutor();
    this.localFolder = localFolder;
  }

  public File importUserV2() {
    File userList = null;
    BufferedWriter output = null;

    DBCollection coll = db.getCollection("ln_user");
    DBCollection coll2 = db.getCollection("ln_oplog_counter");
    DBCursor cursor = null;
    try {
      cursor = coll.find();
      while (cursor.hasNext()) {
        JSONObject userJson = JSONObject.fromObject(MongoDBUtilizer.translateId2String(cursor.next()));
        DBObject queryDbo1 = (DBObject) new BasicDBObject();
        String userID = userJson.getString("_id");
        queryDbo1.put("userID", userID);
        DBObject oplogcounter = MongoDBUtilizer.translateId2String(coll2.findOne(queryDbo1));
        JSONObject oplogJson = JSONObject.fromObject(oplogcounter);
        Map<String, Object> map = DBTransformUtils.transferUserObject(userJson, oplogJson);
        // mapList.add(map);
        queryExecutor.insertData(newDb, "ln_user", map);
        if (importUserCount % defaultLimitNum == 0) {
          int round = importUserCount / defaultLimitNum;
          if (output != null) {
            output.flush();
            output.close();
          }
          userList = new File(localFolder + File.separator + "userInfo_" + round + ".txt");
          output = new BufferedWriter(new FileWriter(userList));
        }
        output.write(userID + "\r\n");
        importUserCount++;
      }
      logger.info("Success Insert new User count:" + importUserCount);
      return userList;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (cursor != null)
        cursor.close();
      try {
        if (output != null)
          output.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  public void executeConcurrentDBUpgrade() {
    // long userCount = 132505;
    int round = 0;
    int limitNum = defaultLimitNum > importUserCount ? importUserCount : defaultLimitNum;
    while (round * limitNum < importUserCount) {
      int startIndex = round * limitNum;
      pool.execute(new DBNoteUpgradeTask(round, db, newDb, startIndex, limitNum));
      round++;
    }
  }

  public void executeFileDBUpgrade() {
    File dirFile = new File(localFolder);
    File[] files = dirFile.listFiles();
    int fileSize = files.length;
    for (int i = 0; i < fileSize; i++) {
      pool.execute(new DBNoteUpgradeTask(i, db, newDb, files[i]));
    }
  }

  public void executeUserDBUpgrade(String userID) {
    long start = System.currentTimeMillis();
    logger.info("Start DB Upgrade V2 task at time:" + start);
    DBNoteUpgradeTask upgradeTask = null;
    upgradeTask = new DBNoteUpgradeTask(1, db, newDb, 0, 1);
    upgradeTask.doWorkByUserID(userID);
    long end = System.currentTimeMillis() - start;
    logger.info("End DB Upgrade V2 task at execute time:" + end);
  }

}
