package com.newroad.data.common.db.mongo;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 20, 2014
 */
public class MongoDBUtilizer {

  public static DBObject translateId2String(DBObject dbo) {
    if (dbo != null && dbo.containsField("_id") && dbo.get("_id") instanceof ObjectId)
      dbo.put("_id", ((ObjectId) dbo.get("_id")).toString());
    return dbo;
  }
}
