package com.newroad.data.transfer.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 12, 2014
 */
public class DBTransformUtils {

  private static Logger logger = LoggerFactory.getLogger(DBTransformUtils.class);

  public static Map<String, Object> transferUserObject(JSONObject userJson, JSONObject oplogJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    String id = userJson.getString("_id");
    map.put("_id", new ObjectId(id));
    if (userJson.has("loginName")) {
      map.put("userName", userJson.getString("loginName"));
    }
    map.put("userType", userJson.getString("userType"));
    map.put("createTime", userJson.getLong("lastLoginTime"));
    // map.put("lastOperateTime", userJson.getString("lastOperateTime"));
    long now = System.currentTimeMillis();
    map.put("lastUpdateTime", now);
    map.put("totalSpace", Double.valueOf(userJson.getLong("totalSpace")).doubleValue());
    // DecimalFormat df = new DecimalFormat("#.00");
    map.put("usedSpace", Double.valueOf(userJson.getLong("usedSpace")).doubleValue());
    if (userJson.has("snsUserID"))
      map.put("snsUserID", userJson.getLong("snsUserID"));
    if (!oplogJson.isNullObject()) {
      map.put("opLogCollection", oplogJson.getString("table"));
      map.put("opLogNum", Long.valueOf(oplogJson.getLong("counter")).longValue());
      map.put("lastOperateTime", oplogJson.getLong("lastUpdateTime"));
    }
    // logger.debug("new User data:" + map);
    return map;
  }

  public static Map<String, Object> transferCategoryObject(JSONObject categoryJson, JSONObject shareJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    String categoryID = categoryJson.getString("_id");
    map.put("_id", new ObjectId(categoryID));
    map.put("categoryName", categoryJson.getString("name"));
    Integer type = categoryJson.getInt("type");
    if (type.equals(1)) {
      map.put("categoryType", 0);
    } else {
      map.put("categoryType", 1);
    }
    map.put("groupName", -1);
    map.put("categoryNoteCount", categoryJson.getInt("numberOfNote"));
    if (categoryJson.has("categoryLogoID")) {
      map.put("categoryIcon", categoryJson.getInt("categoryLogoID"));
    }
    map.put("userID", categoryJson.getString("belong"));
    if (categoryJson.has("parentNode")) {
      if (categoryJson.getString("parentNode").equals("-1")) {
        map.put("categoryType", -1);
      }
    }
    if (!shareJson.isNullObject()) {
      DBObject shareObj = (DBObject) new BasicDBObject();
      shareObj.put("shareToken", shareJson.getString("shareLinkID"));
      shareObj.put("createTime", shareJson.getLong("createTime"));
      shareObj.put("expireTime", shareJson.getLong("expireTime"));
      shareObj.put("status", shareJson.getInt("status"));
      map.put("share", shareObj);
    }
    map.put("createTime", categoryJson.getLong("genTime"));
    map.put("lastUpdateTime", categoryJson.getLong("lastUpdateTime"));
    Integer status = categoryJson.getInt("status");
    if (status.equals(1)) {
      map.put("status", 1);
    } else if (status.equals(0)) {
      map.put("status", 2);
    }
    map.put("version", categoryJson.getInt("version"));
    if (categoryJson.has("dataSource")) {
      Integer dataSourceStr = categoryJson.getInt("dataSource");
      String dataSourceKey = null;
      if (categoryJson.has("dataSourceKey")) {
        dataSourceKey = categoryJson.getString("dataSourceKey");
      }
      DBObject dataSourceObj = (DBObject) new BasicDBObject();
      dataSourceObj.put("dataSource", dataSourceStr);
      dataSourceObj.put("dataSourceKey", dataSourceKey);
      map.put("dataSource", dataSourceObj);
    }
    logger.debug("new Category data:" + map);
    return map;
  }

  public static Map<String, Object> transferNoteObject(JSONObject noteJson, JSONObject shareJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    String id = noteJson.getString("_id");
    map.put("_id", new ObjectId(id));
    map.put("noteType", noteJson.getInt("type"));
    if (noteJson.has("title")) {
      map.put("title", noteJson.getString("title"));
    } else {
      map.put("title", " ");
    }
    if (noteJson.has("summary")) {
      map.put("summary", noteJson.getString("summary"));
    }
    map.put("userID", noteJson.getString("belong"));
    if (noteJson.has("categoryID")) {
      map.put("categoryID", noteJson.getString("categoryID"));
    } else {
      map.put("categoryID", "-1");
    }
    if (noteJson.has("content")) {
      map.put("content", noteJson.getString("content"));
    }
    if (noteJson.has("templateID")) {
      map.put("contentTemplateID", noteJson.getString("templateID"));
    }
    if (noteJson.has("styleType")) {
      map.put("styleType", noteJson.getInt("styleType"));
    }
    if (!shareJson.isNullObject()) {
      DBObject shareObj = (DBObject) new BasicDBObject();
      shareObj.put("shareToken", shareJson.getString("shareLinkID"));
      shareObj.put("createTime", shareJson.getLong("createTime"));
      shareObj.put("expireTime", shareJson.getLong("expireTime"));
      shareObj.put("status", shareJson.getInt("status"));
      map.put("share", shareObj);
    }
    if (noteJson.has("backgroundID")) {
      map.put("backgroundID", noteJson.getString("backgroundID"));
    } else if (noteJson.has("backgroundId")) {
      map.put("backgroundID", noteJson.getString("backgroundId"));
    }
    if (noteJson.has("isMarked")) {
      map.put("isMarked", noteJson.getBoolean("isMarked"));
    } else {
      map.put("isMarked", false);
    }
    if (noteJson.has("mood")) {
      map.put("mood", noteJson.getString("mood"));
    }
    if (noteJson.has("spot")) {
      map.put("spot", noteJson.get("spot"));
    }
    if (noteJson.has("weather")) {
      map.put("weather", noteJson.get("weather"));
    }
    map.put("status", noteJson.getInt("status"));
    map.put("version", noteJson.getInt("version"));
    map.put("userCreateTime", noteJson.getLong("createTime"));
    map.put("createTime", noteJson.getLong("genTime"));
    map.put("lastUpdateTime", noteJson.getLong("lastUpdateTime"));
    if (noteJson.has("dataSource")) {
      Integer dataSourceStr = noteJson.getInt("dataSource");
      String dataSourceKey = null;
      if (noteJson.has("dataSourceKey")) {
        dataSourceKey = noteJson.getString("dataSourceKey");
      }
      DBObject dataSourceObj = (DBObject) new BasicDBObject();
      dataSourceObj.put("dataSource", dataSourceStr);
      dataSourceObj.put("dataSourceKey", dataSourceKey);
      map.put("dataSource", dataSourceObj);
    }
    logger.debug("new Note data:" + map);
    return map;
  }

  public static Map<String, Object> transferResourceObject(JSONObject resourceJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    String id = resourceJson.getString("_id");
    map.put("_id", new ObjectId(id));
    logger.debug("new ResourceID:" + id);
    map.put("clientResourceID", resourceJson.getString("genID"));
    if (resourceJson.has("name")) {
      map.put("resourceName", resourceJson.getString("name"));
    } else {
      map.put("resourceName", " ");
    }
    if (resourceJson.has("description")) {
      map.put("description", resourceJson.getString("description"));
    }
    map.put("resourceType", resourceJson.getInt("type"));
    long size = resourceJson.getLong("size");
    map.put("size", size);

    // FileData
    DBObject fileMap = (DBObject) new BasicDBObject();
    if (resourceJson.has("keyID")) {
      fileMap.put("bucketName", "lenote");
      fileMap.put("keyID", resourceJson.getString("keyID"));
      fileMap.put("fileName", map.get("resourceName"));
      Integer resourceType = (Integer) map.get("resourceType");
      fileMap.put("contentType", FileResourceType.getContentType(resourceType));
      fileMap.put("cloudStatus", 1);
    }
    if (resourceJson.has("link")) {
      fileMap.put("link", resourceJson.getString("link"));
    }
    if (resourceJson.has("publicLink") && resourceJson.getString("publicLink") != null) {
      fileMap.put("publicLink", resourceJson.getString("publicLink"));
    }
    if (resourceJson.has("cosSize")) {
      fileMap.put("cloudSize", resourceJson.getLong("cosSize"));
    } else {
      fileMap.put("cloudSize", size);
    }
    map.put("fileData", fileMap);

    map.put("version", resourceJson.getInt("version"));
    Integer status = resourceJson.getInt("status");
    if (status.equals(0)) {
      map.put("status", 2);
    } else if (status.equals(1)) {
      map.put("status", 1);
    }
    map.put("userID", resourceJson.getString("belong"));
    map.put("noteID", resourceJson.getString("noteID"));
    if (resourceJson.has("mixLocalID")) {
      map.put("clientMixID", resourceJson.getString("mixLocalID"));
    }
    if (resourceJson.has("position")) {
      map.put("position", resourceJson.getString("position"));
    }
    if (resourceJson.has("startTime") && resourceJson.has("fullTime")) {
      map.put("startTime", resourceJson.getLong("startTime"));
      map.put("fullTime", resourceJson.getLong("fullTime"));
    }
    map.put("createTime", resourceJson.getLong("genTime"));
    map.put("lastUpdateTime", resourceJson.getLong("genTime"));
    if (resourceJson.has("uploadTime")) {
      map.put("userCreateTime", resourceJson.getLong("uploadTime"));
    } else {
      map.put("userCreateTime", resourceJson.getLong("genTime"));
    }

    if (resourceJson.has("dataSource")) {
      Integer dataSourceStr = resourceJson.getInt("dataSource");
      String dataSourceKey = null;
      if (resourceJson.has("dataSourceKey")) {
        dataSourceKey = resourceJson.getString("dataSourceKey");
      }
      DBObject dataSourceObj = (DBObject) new BasicDBObject();
      dataSourceObj.put("dataSource", dataSourceStr);
      dataSourceObj.put("dataSourceKey", dataSourceKey);
      map.put("dataSource", dataSourceObj);
    }
    logger.debug("new Resource data:" + map);
    return map;
  }

  public static Map<String, Object> transferTagObject(JSONObject tagJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    String tagID = tagJson.getString("_id");
    map.put("_id", new ObjectId(tagID));
    if (tagJson.has("name")) {
      map.put("tagName", tagJson.getString("name"));
    } else {
      map.put("tagName", " ");
    }
    map.put("tagType", tagJson.getInt("type"));
    if (tagJson.has("tagIcon")) {
      map.put("tagIcon", tagJson.getInt("tagIcon"));
    }
    map.put("tagHash", tagJson.getLong("hash"));
    map.put("tagPath", "/");
    map.put("isMarked", false);
    map.put("userID", tagJson.getString("creator"));
    map.put("status", 1);
    map.put("version", 1);
    long time = tagJson.getLong("createTime");
    map.put("userCreateTime", time);
    map.put("createTime", time);
    map.put("lastUpdateTime", time);
    if (tagJson.has("dataSource")) {
      Integer dataSourceStr = tagJson.getInt("dataSource");
      String dataSourceKey = null;
      if (tagJson.has("dataSourceKey")) {
        dataSourceKey = tagJson.getString("dataSourceKey");
      }
      DBObject dataSourceObj = (DBObject) new BasicDBObject();
      dataSourceObj.put("dataSource", dataSourceStr);
      dataSourceObj.put("dataSourceKey", dataSourceKey);
      map.put("dataSource", dataSourceObj);
    }
    logger.debug("new Tag data:" + map);
    return map;
  }

  public static Map<String, Object> transferOpLogTable(JSONObject opLogTableJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("_id", new ObjectId(opLogTableJson.getString("_id")));
    map.put("collectionName", opLogTableJson.getString("name"));
    map.put("activeLimit", opLogTableJson.getLong("activeLimit"));
    long now = System.currentTimeMillis();
    map.put("createTime", opLogTableJson.getLong("createTime"));
    map.put("lastUpdateTime", now);
    logger.debug("new opLog table data:" + map);
    return map;
  }


  public static Map<String, Object> transferOpLogObject(DBObject dbObj, Map<String, Object> perviousMap) {
    Map<String, Object> map = new HashMap<String, Object>();
    // map.put("_id", new ObjectId(opLogJson.getString("_id")));
    map.put("dataID", dbObj.get("dataID"));
    map.put("userID", dbObj.get("operator"));
    Integer type = null;
    Object typeObj = dbObj.get("type");
    if (typeObj instanceof Double) {
      type = ((Double) dbObj.get("type")).intValue();
    } else {
      type = (Integer) dbObj.get("type");
    }
    switch (type) {
      case 1:
        map.put("dataType", 1);
        map.put("isAvailible", true);
        break;
      case 2:
        map.put("dataType", 1);
        map.put("isAvailible", true);
        break;
      case 4:
        map.put("dataType", 1);
        map.put("isAvailible", false);
        break;
      case 8:
        map.put("dataType", 2);
        map.put("isAvailible", true);
        break;
      case 16:
        map.put("dataType", 2);
        map.put("isAvailible", true);
        break;
      case 32:
        map.put("dataType", 2);
        map.put("isAvailible", false);
        break;
      case 64:
        map.put("dataType", 3);
        map.put("isAvailible", true);
        break;
    }

    Long currentOpertaion = (Long) dbObj.get("operateTime");
    Long currentSysTime = (Long) dbObj.get("sysTime");
    if (perviousMap == null || perviousMap.size() == 0) {
      map.put("createTimeStamp", currentOpertaion);
      map.put("timeStamp", currentOpertaion);
      map.put("createTime", currentSysTime);
      map.put("lastUpdateTime", currentSysTime);
    } else {
      Long perviousCreateTimeStamp = (Long) perviousMap.get("createTimeStamp");
      Long perviousTimeStamp = (Long) perviousMap.get("timeStamp");
      if (currentOpertaion < perviousCreateTimeStamp) {
        map.put("createTimeStamp", currentOpertaion);
        map.put("timeStamp", perviousTimeStamp);
      } else if (currentOpertaion >= perviousCreateTimeStamp && currentOpertaion <= perviousTimeStamp) {
        map.put("createTimeStamp", perviousCreateTimeStamp);
        map.put("timeStamp", perviousTimeStamp);
      } else if (currentOpertaion > perviousTimeStamp) {
        map.put("createTimeStamp", perviousCreateTimeStamp);
        map.put("timeStamp", currentOpertaion);
      }

      Long perviousCreateTime = (Long) perviousMap.get("createTime");
      Long perviousLastUpdateTime = (Long) perviousMap.get("lastUpdateTime");
      if (currentSysTime < perviousCreateTime) {
        map.put("createTime", currentSysTime);
        map.put("lastUpdateTime", perviousLastUpdateTime);
      } else if (currentSysTime >= perviousCreateTime && currentSysTime <= perviousLastUpdateTime) {
        map.put("createTime", perviousCreateTime);
        map.put("lastUpdateTime", perviousLastUpdateTime);
      } else if (currentSysTime > perviousLastUpdateTime) {
        map.put("createTime", perviousCreateTime);
        map.put("lastUpdateTime", currentSysTime);
      }
    }
    logger.debug("new opLog data:" + map);
    return map;
  }

  public static Map<String, Object> transferDefaultCategoryOpLog(JSONObject categoryJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("dataID", categoryJson.getString("_id"));
    map.put("dataType", 2);
    map.put("isAvailible", true);
    map.put("createTimeStamp", 1);
    map.put("timeStamp", 1);
    map.put("userID", categoryJson.getString("userID"));
    long now = System.currentTimeMillis();
    map.put("createTime", now);
    map.put("lastUpdateTime", now);
    logger.debug("default category opLog data:" + map);
    return map;
  }

  public static Map<String, Object> transferNewResourceOpLog(JSONObject resourceJson) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("dataID", resourceJson.getString("_id"));
    map.put("dataType", 4);
    map.put("isAvailible", true);
    map.put("createTimeStamp", resourceJson.getLong("createTime"));
    map.put("timeStamp", resourceJson.getLong("createTime"));
    map.put("userID", resourceJson.getString("userID"));
    long now = System.currentTimeMillis();
    map.put("createTime", now);
    map.put("lastUpdateTime", now);
    logger.debug("new resource opLog data:" + map);
    return map;
  }

}
