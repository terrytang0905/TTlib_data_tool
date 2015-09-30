package com.newroad.data.common.db.mongo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 9, 2014
 */
public class MongoConnectionFactory {

  private static Logger logger = LoggerFactory.getLogger(MongoConnectionFactory.class);

  public static String dbName1 = null;
  public static String dbName2 = null;
  public static String[] collectionArray = new String[] {"ln_note", "ln_category", "ln_resource", "ln_tag"};

  private static Properties propertie;
  private static String[] nodeiplist = null;
  private static String[] nodeportlist = null;

  private static String userName = null;
  private static String passWord = null;
  private static Integer connectionsPerHost;
  private static Integer threadsAllowedToBlock;
  private static Integer connectionTimeOut = 15 * 1000;
  private static Integer maxRetryTime = 15 * 1000;
  private static Integer socketTimeOut = 60 * 1000;

  private static MongoManager mongoManager;
  private static MongoManager mongoManager2;

  static {
    propertie = new Properties();
    try {
      String filePath = "mongo.properties";
      propertie.load(MongoConnectionFactory.class.getClassLoader().getResourceAsStream(filePath));
      nodeiplist = propertie.getProperty("product.mongo.db.nodeiplist").split(",");
      nodeportlist = propertie.getProperty("product.mongo.db.nodeportlist").split(",");
      dbName1 = propertie.getProperty("product.mongo.db.dbname1");
      dbName2 = propertie.getProperty("product.mongo.db.dbname2");
      userName = propertie.getProperty("product.mongo.db.username");
      passWord = propertie.getProperty("product.mongo.db.password");
      connectionsPerHost = Integer.valueOf(propertie.getProperty("product.mongo.db.connectionsperhost"));
      threadsAllowedToBlock = Integer.valueOf(propertie.getProperty("product.mongo.db.threadsallowedtoblock"));
      maxRetryTime = Integer.valueOf(propertie.getProperty("product.mongo.db.maxretrytime"));
      socketTimeOut = Integer.valueOf(propertie.getProperty("product.mongo.db.sockettimeout"));
    } catch (FileNotFoundException ex) {
      logger.error("DBConnection FileNotFoundException:", ex);
    } catch (IOException ex) {
      logger.error("DBConnection IOException:", ex);
    } catch (Exception e) {
      logger.error("DBConnection Exception:", e);
    }
  }

  public static DB getDefaultDB() {
    try {
      if (mongoManager == null) {
        mongoManager =
            new MongoManager(nodeiplist, nodeportlist, dbName1, userName, passWord, connectionsPerHost, threadsAllowedToBlock,
                connectionTimeOut, maxRetryTime, socketTimeOut);
      }
      return mongoManager.getDB();
    } catch (Exception e) {
      logger.error("DBConnection Exception:", e);
    }
    return null;
  }

  public static DB getDB(String dbName) {
    try {
      if (dbName.equals(dbName1)) {
        if (mongoManager == null) {
          mongoManager =
              new MongoManager(nodeiplist, nodeportlist, dbName, userName, passWord, connectionsPerHost, threadsAllowedToBlock,
                  connectionTimeOut, maxRetryTime, socketTimeOut);
        }
        return mongoManager.getDB();
      } else if (dbName.equals(dbName2)) {
        if (mongoManager2 == null) {
          mongoManager2 =
              new MongoManager(nodeiplist, nodeportlist, dbName, userName, passWord, connectionsPerHost, threadsAllowedToBlock,
                  connectionTimeOut, maxRetryTime, socketTimeOut);
        }
        return mongoManager2.getDB();
      }
    } catch (Exception e) {
      logger.error("DBConnection Exception:", e);
    }
    return null;
  }
}
