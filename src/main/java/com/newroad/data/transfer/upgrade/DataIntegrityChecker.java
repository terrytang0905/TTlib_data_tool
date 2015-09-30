package com.newroad.data.transfer.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.transfer.utils.DBTransformUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class DataIntegrityChecker {

  private static Logger logger = LoggerFactory.getLogger(DataIntegrityChecker.class);

  private DB db;
  private DB newDb;
  private String[] collectionArray;
  private String errorList;
  private String reloadUserList;
  private String invalidUserList;
  private static List<String> tempList = new ArrayList<String>();;

  public DataIntegrityChecker(DB db, DB newDb) {
    this.db = db;
    this.newDb = newDb;
  }

  public DataIntegrityChecker(String dbName1, String dbName2, String errorList, String reloadUserList, String invalidUserList) {
    this.db = MongoConnectionFactory.getDB(dbName1);
    this.newDb = MongoConnectionFactory.getDB(dbName2);
    this.collectionArray = MongoConnectionFactory.collectionArray;
    this.errorList = errorList;
    this.reloadUserList = reloadUserList;
    this.invalidUserList = invalidUserList;
  }

  public void checkDefaultDataIntegrity() {
    File errorFile = new File(errorList);
    File reloadUserFile = new File(reloadUserList);
    File invalidUserFile = new File(invalidUserList);
    BufferedWriter output = null;
    BufferedWriter output2 = null;
    BufferedWriter output3 = null;
    try {
      output = new BufferedWriter(new FileWriter(errorFile));
      output2 = new BufferedWriter(new FileWriter(reloadUserFile));
      output3 = new BufferedWriter(new FileWriter(invalidUserFile));
      for (String collection : collectionArray) {
        checkDataIntegrity(output, output2, output3, collection);
      }
      output.flush();
      output2.flush();
      output3.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
        output2.close();
        output3.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    logger.info("Data Integrity Checker has been completed!");
  }

  public void checkOplogIntegrity() {
    File errorFile = new File(errorList);
    BufferedWriter output = null;
    try {
      output = new BufferedWriter(new FileWriter(errorFile));
      for (String collection : collectionArray) {
        DBCollection coll = newDb.getCollection(collection);
        DBCollection coll2 = newDb.getCollection("ln_oplog");
        DBCursor cursor = coll.find();
        while (cursor.hasNext()) {
          DBObject object = cursor.next();
          String id = object.get("_id").toString();
          DBObject oplogObj = coll2.findOne(new BasicDBObject("dataID", id));
          if (oplogObj == null) {
            output.write("Oplog missing from " + collection + " & ID=" + id + "\r\n");
          }
        }
      }
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
    }
    logger.info("Oplog Data Integrity Checker has been completed!");
  }

  public void syncOplogIntegrity() {
    File errorFile = new File(errorList);
    BufferedReader input = null;
    try {
      input = new BufferedReader(new FileReader(errorFile));
      DBCollection coll1 = db.getCollection("ln_oplog");
      DBCollection coll2 = newDb.getCollection("ln_oplog");
      String str = null;
      while ((str = input.readLine()) != null) {
        // int startC=str.indexOf("ln");
        // int endC=str.indexOf("&");
        // String collection=str.substring(startC, endC);
        int startD = str.indexOf("Id=");
        String dataID = str.substring(startD+3);

        DBCursor cursor = coll1.find(new BasicDBObject("dataID", dataID));
        Map<String, Object> dataMap = null;
        while (cursor.hasNext()) {
          DBObject dbObj = cursor.next();
          dataMap = DBTransformUtils.transferOpLogObject(dbObj, dataMap);
        }

        if (dataMap != null) {
          DBObject dbobj = (DBObject) new BasicDBObject();
          dbobj.putAll(dataMap);
          coll2.insert(dbobj, WriteConcern.SAFE);
          logger.info("Oplog dataID:"+dataID+" insert completed!");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {

    }
    logger.info("Oplog Data Integrity Checker has been completed!");
  }


  private void checkDataIntegrity(BufferedWriter output, BufferedWriter output2, BufferedWriter output3, String collection) {
    DBCollection coll = db.getCollection(collection);
    DBCollection coll2 = newDb.getCollection(collection);
    long oldCount = coll.count();
    long newCount = coll2.count();
    if (oldCount == newCount) {
      logger.info("The count of new collection " + collection + "(" + newCount + ") is consistent with old collection!");
      return;
    }

    int errorCount = 0;
    DBCollection coll3 = db.getCollection("ln_user");
    DBCursor cursor = null;
    try {
      cursor = coll.find();
      while (cursor.hasNext()) {
        DBObject oldObject = cursor.next();
        DBObject newObject = coll2.findOne(oldObject.get("_id"));
        if (newObject == null) {
          errorCount++;
          String id = oldObject.get("_id").toString();
          String userID = (String) oldObject.get("belong");
          if ("ln_tag".equals(collection)) {
            userID = (String) oldObject.get("creator");
          }
          String errorMessage =
              "The id Object " + id + " couldn't be found in the new collection " + collection + " by user " + userID + ", errorCount:"
                  + errorCount;
          output.write(errorMessage + "\r\n");
          if (!tempList.contains(userID) && userID != null) {
            tempList.add(userID);
            DBObject userObj = coll3.findOne(new BasicDBObject("_id", new ObjectId(userID)));
            if (userObj != null) {
              output2.write(userID + "\r\n");
            } else {
              output3.write(userID + "\r\n");
            }
          }
          logger.info(errorMessage);
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (cursor != null)
        cursor.close();
    }

  }
}
