package com.newroad.data.common.db.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * @info : Mongo DB 管理器
 * @author: tangzj1
 * @data : 2013-10-10
 * @since : 2.0
 */
public class MongoManager {

  private static Logger log = LoggerFactory.getLogger(MongoManager.class);

  private String[] nodeiplist = null;
  private String[] nodeportlist = null;
  private String dbName = null;
  private String userName = null;
  private String passWord = null;
  private Integer connectionsPerHost;
  private Integer threadsAllowedToBlock;
  private Integer connectionTimeOut = 15 * 1000;
  private Integer maxRetryTime = 15 * 1000;
  private Integer socketTimeOut = 60 * 1000;
  private List<ServerAddress> serverAddressList;

  private MongoClient mongoClient;

  public MongoManager(String[] nodeiplist, String[] nodeportlist, String dbName, String userName,
      String passWord, Integer connectionsPerHost, Integer threadsAllowedToBlock,
      Integer connectionTimeOut, Integer maxRetryTime, Integer socketTimeOut) throws Exception {
    if (mongoClient == null) {
      this.nodeiplist = nodeiplist;
      this.nodeportlist = nodeportlist;
      this.dbName = dbName;
      this.userName = userName;
      this.passWord = passWord;
      this.connectionsPerHost = connectionsPerHost;
      this.threadsAllowedToBlock = threadsAllowedToBlock;
      this.connectionTimeOut = connectionTimeOut;
      this.maxRetryTime = maxRetryTime;
      this.socketTimeOut = socketTimeOut;
      serverAddressList = new ArrayList<ServerAddress>();
      try {
        for (int i = 0; i < nodeiplist.length; i++) {
          serverAddressList
              .add(new ServerAddress(nodeiplist[i], Integer.parseInt(nodeportlist[i])));
        }
      } catch (NumberFormatException e) {
        // TODO Auto-generated catch block
        log.error("MongoManager NumberFormatException:" + e);
      }
      mongoInit();
    }
  }

//	public static MongoManager getInstance() {
//		return InnerHolder.INSTANCE;
//	}
//
//	private static class InnerHolder {
//		static final MongoManager INSTANCE = new MongoManager();
//	}

  private void mongoInit() throws Exception {
    MongoClientOptions.Builder builder =
        MongoClientOptions.builder().connectionsPerHost(connectionsPerHost)
            .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlock)
            .connectTimeout(connectionTimeOut).socketTimeout(socketTimeOut);
    if (maxRetryTime > 0) {
      builder.maxWaitTime(maxRetryTime);
    }

    int nodes = serverAddressList.size();
    MongoCredential credential =
        MongoCredential.createMongoCRCredential(userName, dbName, passWord.toCharArray());
    if (nodes <= 0) {
      mongoClient = null;
      throw new Exception("Couldn't get available mongo server address!");
    } else if (nodes == 1) {
      mongoClient =
          new MongoClient(serverAddressList.get(0), Arrays.asList(credential), builder.build());
    } else {
      mongoClient = new MongoClient(serverAddressList, Arrays.asList(credential), builder.build());
    }

    //MongoDatabase dbpool = mongoClient.getDatabase(dbName);
  }

  private ThreadLocal<DB> local = new ThreadLocal<DB>();

  public DB getDB() {
    DB db = local.get();
    if (db != null) {
      return db;
    }
    return mongoClient.getDB(dbName);
  }

  public void closeDB() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }

}
