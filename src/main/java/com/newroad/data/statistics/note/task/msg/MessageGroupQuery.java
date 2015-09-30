package com.newroad.data.statistics.note.task.msg;

import com.newroad.data.common.db.mysql.MySqlDao;
import com.newroad.cache.common.couchbase.CouchbaseCache;

public interface MessageGroupQuery {

  public static final String designDocName = "MessageDesignDoc";
  public static final String viewName = "MessageCountView";

  public void messageGroupStatistics(CouchbaseCache cache,MySqlDao mysqlDao, String messageID);
}
