package com.newroad.data.common.db.couchbase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.cache.common.couchbase.CouchbaseCache;


public class CouchbaseManager {

  private static Logger logger = LoggerFactory.getLogger(CouchbaseManager.class);

  private static Properties propertie;

  private static String nodeList = null;
  private static String bucketName = null;
  private static String bucketPwd = null;
  private static Long reconnect = null;
  private static Long opTimeout = null;
  private static Long opQueueMaxBlockTime = null;

  private static CouchbaseCache cache;

  static {
    propertie = new Properties();
    try {
      String filePath = "couchbase.properties";
      propertie.load(CouchbaseManager.class.getClassLoader().getResourceAsStream(filePath));
      nodeList = propertie.getProperty("COUCHBASE_NODELIST");
      bucketName = propertie.getProperty("COUCHBASE_BUCKETNAME");
      bucketPwd = propertie.getProperty("COUCHBASE_BUCKET_PWD");
      reconnect = Long.valueOf(propertie.getProperty("COUCHBASE_RECONNECT"));
      opTimeout = Long.valueOf(propertie.getProperty("COUCHBASE_OP_TIMEOUT"));
      opQueueMaxBlockTime = Long.valueOf(propertie.getProperty("COUCHBASE_OP_QUEUE_MAXBLOCKTIME"));
    } catch (FileNotFoundException ex) {
      logger.error("DBConnection FileNotFoundException:", ex);
    } catch (IOException ex) {
      logger.error("DBConnection IOException:", ex);
    } catch (Exception e) {
      logger.error("DBConnection Exception:", e);
    }
  }

  public static CouchbaseCache getInstance() {
    if (cache == null) {
      cache = new CouchbaseCache(nodeList, bucketName, bucketPwd, opTimeout, opQueueMaxBlockTime, reconnect, 1, 0);
    }
    return cache;
  }
}
