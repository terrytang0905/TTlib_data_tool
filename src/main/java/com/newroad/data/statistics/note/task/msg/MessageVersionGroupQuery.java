package com.newroad.data.statistics.note.task.msg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.cache.common.couchbase.CouchbaseCache;
import com.newroad.cache.common.couchbase.CouchbaseViewService;
import com.newroad.data.statistics.datamodel.message.MessageViewStatistics;
import com.newroad.data.common.db.mysql.MySqlDao;

public class MessageVersionGroupQuery implements MessageGroupQuery {

  private static final Logger logger = LoggerFactory.getLogger(MessageVersionGroupQuery.class);
  
  @Override
  public void messageGroupStatistics(CouchbaseCache cache, MySqlDao mysqlDao, String messageID) {
    logger.info("MessageVersionGroupQuery for messageID="+messageID);
    
    CouchbaseViewService viewSerive = new CouchbaseViewService();
    viewSerive.setCouchbaseCache(cache);

    Map<String, Object> queryMap = new HashMap<String, Object>();
    queryMap.put("msgID", messageID);

    Map<String, Object> result = viewSerive.countGroupView(designDocName, viewName, queryMap, new String[] {"version"}, null, null);
    Set<Entry<String, Object>> resultSet = result.entrySet();
    Iterator<Entry<String, Object>> iter = resultSet.iterator();
    while (iter.hasNext()) {
      Entry<String, Object> entry = iter.next();
      String key = entry.getKey();
      Long count = (Long) entry.getValue();
      MessageViewStatistics viewStats = new MessageViewStatistics(messageID, key, count);
    }
    logger.info("Count Version Group View Result:" + result);
  }

}
