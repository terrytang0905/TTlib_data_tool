package com.newroad.data.common.db.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 20, 2014
 */
public class MongoQueryExecutor {

  public void insertData(DB db, String collection, Map<String, Object> map) {
    DBCollection coll = db.getCollection(collection);
    DBObject dbobj = (DBObject) new BasicDBObject();
    dbobj.putAll(map);
    coll.insert(dbobj, WriteConcern.SAFE);
  }
  
  public void findInsertData(DB db, String collection, Map<String, Object> queryMap,Map<String,Object> updateMap) {
    DBCollection coll = db.getCollection(collection);
    DBObject dbobj = (DBObject) new BasicDBObject();
    dbobj.putAll(queryMap);
    
    DBObject dbobj2 = (DBObject) new BasicDBObject();
    dbobj.putAll(updateMap);
    coll.findAndModify(dbobj, null, null, false, dbobj2, true, true);
  }

  public void batchInsertData(DB db, String collection, List<Map<String, Object>> mapList) {
    DBCollection coll = db.getCollection(collection);
    List<DBObject> dbObjects = new ArrayList<DBObject>(mapList.size());
    for (Map<String, Object> map : mapList) {
      DBObject dbobj = (DBObject) new BasicDBObject();
      dbobj.putAll(map);
      dbObjects.add(dbobj);
    }
    coll.insert(dbObjects, WriteConcern.SAFE);
  }

  public void updateData(DB db, String collection, Map<String, Object> queryMap, Map<String, Object> updateMap) {
    DBCollection coll = db.getCollection(collection);
    DBObject queryObj = (DBObject) new BasicDBObject();
    queryObj.putAll(queryMap);

    DBObject updateSet = (DBObject) new BasicDBObject();
    updateSet.put("$set", updateMap);
    coll.update(queryObj, updateSet, false, true);
  }
  
  public void deleteData(DB db, String collection, Map<String, Object> queryMap) {
    DBCollection coll = db.getCollection(collection);
    DBObject queryObj = (DBObject) new BasicDBObject();
    queryObj.putAll(queryMap);
    
    coll.remove(queryObj, WriteConcern.SAFE);
  }
}
