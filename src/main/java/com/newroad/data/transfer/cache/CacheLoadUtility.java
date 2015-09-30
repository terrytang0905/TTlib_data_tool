package com.newroad.data.transfer.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.cache.common.couchbase.CouchbaseCache;
import com.newroad.cache.common.couchbase.CouchbaseProperties;
import com.newroad.cache.common.memcached.MemcachedCache;
import com.newroad.cache.common.memcached.MemcachedProperties;


/**
 * @info
 * @author tangzj1
 * @date Feb 12, 2014
 * @version
 */
public class CacheLoadUtility {

  private static Logger LOG = LoggerFactory.getLogger(CacheLoadUtility.class);

  private static Properties properties;
  private MemcachedCache memcached;
  private CouchbaseCache couchbase;

  private static MemcachedProperties memProp;

  // Couchbase
  private static String COUCHBASE_SERVER_NODELIST;
  private static String COUCHBASE_SERVER_BUCKETNAME;
  private static String COUCHBASE_SERVER_PWD;
  private static long COUCHBASE_SERVER_OP_TIMEOUT;
  private static long COUCHBASE_SERVER_OP_QUEUE_MAXBLOCKTIME;
  private static long COUCHBASE_SERVER_RECONNECT = 1000;

  // Memcached
  private static String MEM_SERVER_LIST;
  private static String MEM_SERVER_NAME;
  private static int MEM_SERVER_INIT_CONN;
  private static int MEM_SERVER_MAX_CONN;
  private static int MEM_SERVER_MIN_CONN;

  static {
    properties = new Properties();
    try {
      String filePath = "cache.properties";
      properties.load(CouchbaseProperties.class.getClassLoader().getResourceAsStream(filePath));
      MEM_SERVER_LIST = properties.getProperty("MEM_SERVER_LIST");
      MEM_SERVER_NAME = properties.getProperty("MEM_SERVER_NAME");
      MEM_SERVER_INIT_CONN = Integer.valueOf(properties.getProperty("MEM_SERVER_INIT_CONN"));
      MEM_SERVER_MAX_CONN = Integer.valueOf(properties.getProperty("MEM_SERVER_MAX_CONN"));
      MEM_SERVER_MIN_CONN = Integer.valueOf(properties.getProperty("MEM_SERVER_MIN_CONN"));

      COUCHBASE_SERVER_NODELIST = properties.getProperty("COUCHBASE_SERVER_NODELIST");
      COUCHBASE_SERVER_BUCKETNAME = properties.getProperty("COUCHBASE_SERVER_BUCKETNAME");
      COUCHBASE_SERVER_PWD = properties.getProperty("COUCHBASE_SERVER_PWD");
      COUCHBASE_SERVER_OP_TIMEOUT = Long.valueOf(properties.getProperty("COUCHBASE_SERVER_OP_TIMEOUT"));
      COUCHBASE_SERVER_OP_QUEUE_MAXBLOCKTIME = Long.valueOf(properties.getProperty("COUCHBASE_SERVER_OP_QUEUE_MAXBLOCKTIME"));
      COUCHBASE_SERVER_RECONNECT = Long.valueOf(properties.getProperty("COUCHBASE_SERVER_RECONNECT"));
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void loadMemcached(String tempFilePath) {
    File csv = new File(tempFilePath);
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(csv, true));
      memcached = new MemcachedCache(memProp);
      // for (int i = 0; i < 5000; i++) {
      // memcached
      // .set("xiaoyu" + i, new User("xiaoyu", 25, false, "it"));
      // memcached.set("tangzj" + i, new User("xiaoyu", 32, true, "it"));
      // memcached.set("gejian" + i, new User("xiaoyu", 32, true, "it"));
      // memcached.set("xupeng" + i, new User("xiaoyu", 30, true, "it"));
      // }

      long size = memcached.size();
      Map<String, String> keyMap = memcached.keysExpire();
      int mapSize = keyMap.size();
      System.out.println("Total Cache Size:" + size + ";dump Size:" + mapSize);
      // if (size == mapSize) {
      for (Iterator<String> itemIt = keyMap.keySet().iterator(); itemIt.hasNext();) {
        String key = itemIt.next();
        Serializable value = memcached.get(key);
        long expire = Long.valueOf(keyMap.get(key));
        String cache = key + "," + value + "," + expire;
        LOG.info(cache);
        bw.write(cache);
        bw.newLine();
      }
      // } else if (mapSize < size) {
      // subLoadMemcached(size, bw);
      // }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unused")
  private void subLoadMemcached(long size, BufferedWriter bw) throws IOException {
    Map<String, String> keyMap = memcached.keysExpire();
    int mapSize = keyMap.size();
    for (Iterator<String> itemIt = keyMap.keySet().iterator(); itemIt.hasNext();) {
      String key = itemIt.next();
      Serializable value = memcached.get(key);
      long expire = Long.valueOf(keyMap.get(key));
      String cache = key + "," + value + "," + expire;
      System.out.println(cache);
      bw.write(cache);
      bw.newLine();
      memcached.delete(key);
    }
    long diff = size - mapSize;
    if (diff <= 0) {
      return;
    } else if (diff > 0) {
      subLoadMemcached(diff, bw);
    }
  }

  public void syncMemcachedCouchbase() {
    File csv = new File("memcachedload.csv");
    BufferedWriter bw = null;

    memcached = new MemcachedCache(memProp);
    couchbase =
        new CouchbaseCache(COUCHBASE_SERVER_NODELIST, COUCHBASE_SERVER_BUCKETNAME, COUCHBASE_SERVER_PWD, COUCHBASE_SERVER_OP_TIMEOUT,
            COUCHBASE_SERVER_OP_QUEUE_MAXBLOCKTIME, COUCHBASE_SERVER_RECONNECT,1,0);

    // for (int i = 0; i < 5000; i++) {
    // memcached.set("newresult13" + i, new User("xiaoyu", 25, false,
    // "it"));
    // memcached.set("newresult14" + i, new User("xiaoyu", 32, true, "it"));
    // memcached.set("newresult15" + i, new User("xiaoyu", 32, true, "it"));
    // memcached.set("newresult16" + i, new User("xiaoyu", 30, true, "it"));
    // }

    Calendar extime = Calendar.getInstance();
    extime.set(2013, 0, 1, 0, 0, 0);
    long createTime = extime.getTimeInMillis();

    long size = memcached.size();
    Map<String, String> keyMap;
    try {
      bw = new BufferedWriter(new FileWriter(csv, true));

      keyMap = memcached.keysExpire();
      int mapSize = keyMap.size();
      System.out.println("Total Cache Size:" + size + ";dump Size:" + mapSize);
      if (size > 0) {
        if (size == mapSize) {
          for (Iterator<String> itemIt = keyMap.keySet().iterator(); itemIt.hasNext();) {
            String key = itemIt.next();
            Serializable value = memcached.get(key);
            String expire = keyMap.get(key);
            long expireTime = Long.valueOf(expire);
            if (value == null) {
              System.out.println("The value of key in memcached " + key + " is null!");
              continue;
            }
            if (expireTime > createTime) {
              couchbase.set(key, value, Long.valueOf(expire));
            } else {
              couchbase.set(key, value);
            }
            Serializable cacheValue = couchbase.get(key);
            String cache = key + "," + cacheValue + "," + expire;
            // System.out.println(cache);
            bw.write(cache);
            bw.newLine();
          }
        } else if (mapSize < size) {
          deepSyncMemcachedCouchbase(createTime, bw);
        }
      }
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    couchbase.shutDown();
  }

  private void deepSyncMemcachedCouchbase(long createTime, BufferedWriter bw) throws IOException {
    long cacheSize = memcached.size();
    Map<String, String> keyMap = memcached.keysExpire();
    int mapSize = keyMap.size();
    for (Iterator<String> itemIt = keyMap.keySet().iterator(); itemIt.hasNext();) {
      String key = itemIt.next();
      Serializable value = memcached.get(key);
      String expire = keyMap.get(key);
      long expireTime = Long.valueOf(expire);
      if (value == null) {
        System.out.println("The value of key in memcached " + key + " is null!");
        continue;
      }
      if (expireTime > createTime) {
        couchbase.set(key, value, Long.valueOf(expire));
      } else {
        couchbase.set(key, value);
      }
      Serializable cacheValue = couchbase.get(key);
      String cache = key + "," + cacheValue + "," + expire;
      // System.out.println(cache);
      bw.write(cache);
      bw.newLine();
      memcached.delete(key);
    }
    long diff = cacheSize - mapSize;
    System.out.println("Total Cache Size:" + cacheSize + ";dump Size:" + mapSize);
    if (diff <= 0) {
      return;
    } else if (diff > 0) {
      deepSyncMemcachedCouchbase(createTime, bw);
    }
  }

  public void init() {
    memProp = new MemcachedProperties(MEM_SERVER_LIST, MEM_SERVER_NAME, MEM_SERVER_INIT_CONN, MEM_SERVER_MAX_CONN, MEM_SERVER_MIN_CONN);
  }

  public static void main(String[] args) {
    CacheLoadUtility cacheTransfer = new CacheLoadUtility();
    cacheTransfer.init();
    // String tempFilePath = "memcached.csv";
    // cacheTransfer.loadMemcached(tempFilePath);
    cacheTransfer.syncMemcachedCouchbase();
    // cacheTransfer.queryCouchbaseView();
  }

}
