package com.newroad.data.statistics.note.cloudresource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import net.sf.json.JSONObject;

import com.newroad.data.common.db.mongo.MongoManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @info
 * @author tangzj1
 * @date Jan 2, 2014
 * @version
 */
public class ResourceAnalyzer {

  private static Properties propertie;

  private static String[] nodeiplist = null;
  private static String[] nodeportlist = null;
  private static String dbName = null;
  private static String userName = null;
  private static String passWord = null;
  private static Integer connectionsPerHost;
  private static Integer threadsAllowedToBlock;
  private static Integer connectionTimeOut = 15 * 1000;
  private static Integer maxRetryTime = 15 * 1000;
  private static Integer socketTimeOut = 60 * 1000;

  private static MongoManager mongoManager;

  static {
    propertie = new Properties();
    try {
      String filePath = "config.properties";
      propertie.load(ResourceAnalyzer.class.getClassLoader().getResourceAsStream(filePath));
      nodeiplist = propertie.getProperty("product.mongo.db.nodeiplist").split(",");
      nodeportlist = propertie.getProperty("product.mongo.db.nodeportlist").split(",");
      dbName = propertie.getProperty("product.mongo.db.dbname");
      userName = propertie.getProperty("product.mongo.db.username");
      passWord = propertie.getProperty("product.mongo.db.password");
      connectionsPerHost = Integer.valueOf(propertie.getProperty("product.mongo.db.connectionsperhost"));
      threadsAllowedToBlock = Integer.valueOf(propertie.getProperty("product.mongo.db.threadsallowedtoblock"));

      mongoManager =
          new MongoManager(nodeiplist, nodeportlist, dbName, userName, passWord, connectionsPerHost, threadsAllowedToBlock,
              connectionTimeOut, maxRetryTime, socketTimeOut);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void filterDataByCSV(String sourceFileName, String outputFileName) {
    File csv = new File(outputFileName); // CSV数据文件
    BufferedReader reader = null;
    BufferedWriter bw = null;
    try {
      String path = this.getClass().getResource("/" + sourceFileName).getPath();
      reader = new BufferedReader(new FileReader(path));// 换成你的文件名
      bw = new BufferedWriter(new FileWriter(csv, true)); //
      bw.write("key, size, dbSize, diff");
      bw.newLine();

      reader.readLine();// 第一行信息，为标题信息，不用,如果需要，注释掉
      String line = null;
      while ((line = reader.readLine()) != null) {
        String item[] = line.split(",");
        String key = item[0].replace("\"", "");
        Long size = Long.valueOf(item[1]);
        // Long dbsize = Long.valueOf(item[2]);
        // Long diff = Long.valueOf(item[3]);
        if (size == 194) {
          bw.write(key + "," + size);
          bw.newLine();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void searchDataByCSV(String sourceFileName, String outputFileName) throws Exception {
    File csv = new File(outputFileName); // CSV数据文件
    BufferedReader reader = null;
    BufferedWriter bw = null;
    try {
      String path = this.getClass().getResource("/" + sourceFileName).getPath();
      reader = new BufferedReader(new FileReader(path));// 换成你的文件名
      bw = new BufferedWriter(new FileWriter(csv, true)); //
      // bw.write("key, size, dbSize, diff");
      // bw.newLine();

      reader.readLine();// 第一行信息，为标题信息，不用,如果需要，注释掉
      String line = null;
      while ((line = reader.readLine()) != null) {
        String item[] = line.split(",");
        String key = item[0].replace("\"", "");
        Long size = Long.valueOf(item[1]);
        JSONObject json = checkDBResource(key);
        Long dbsize = 0l;
        if (!json.isNullObject()) {
          String belong = json.getString("belong");
          String dbSize = json.getString("size");
          dbsize = Double.valueOf(dbSize).longValue();
          Long diff = size - dbsize;
          if (diff != 0) {
            String info = key + "," + size + "," + dbsize + "," + diff + "," + belong;
            bw.write(info);
            // System.out.println(info);
            bw.newLine();
            bw.flush();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void updateData(String fileName, String outputFileName) {
    File csv = new File(outputFileName);
    BufferedReader reader = null;
    BufferedWriter bw = null;
    try {
      String path = this.getClass().getResource("/" + fileName).getPath();
      reader = new BufferedReader(new FileReader(path));// 换成你的文件名
      bw = new BufferedWriter(new FileWriter(csv, true)); //

      reader.readLine();// 第一行信息，为标题信息，不用,如果需要，注释掉
      String line = null;
      while ((line = reader.readLine()) != null) {
        String item[] = line.split(",");
        String key = item[0].replace("\"", "");
        Long size = Long.valueOf(item[1]);
        Long dbsize = Long.valueOf(item[2]);
        Long diff = Long.valueOf(item[3]);
        // String type = item[4];

        if (size == 194 && diff != 0) {
          JSONObject json = findAndModifyDBResource(key, 0l);
          String belong = json.getString("belong");
          String dbSize = json.getString("size");
          String info = key + "," + size + "," + dbSize + "," + diff + "," + belong;
          bw.write(info);
          // System.out.println(info);
          bw.newLine();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void createOpLog() {

  }

  private JSONObject checkDBResource(String key) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection("ln_resource");

    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.put("keyID", key);

    DBObject result = coll.findOne(queryDbo, null);
    return JSONObject.fromObject(result);
  }

  private JSONObject findAndModifyDBResource(String key, Long size) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection("ln_resource");

    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.put("keyID", key);

    BasicDBObject newDocument = new BasicDBObject();
    newDocument.put("size", size);

    BasicDBObject updateObj = new BasicDBObject();
    updateObj.put("$set", newDocument);

    DBObject object = coll.findAndModify(queryDbo, updateObj);
    return JSONObject.fromObject(object);
  }

  public Long logOpUpdateNote(String noteId) {
    return null;
  }

}
