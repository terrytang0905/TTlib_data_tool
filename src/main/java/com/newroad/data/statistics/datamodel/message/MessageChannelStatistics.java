package com.newroad.data.statistics.datamodel.message;

import java.util.Date;

public class MessageChannelStatistics {
  private String id;
  private String messageId;
  private String channel;
  private Long vaildCount;
  private Date systemTime;
  
  public MessageChannelStatistics(String messageId, String channel, Long vaildCount) {
    super();
    this.messageId = messageId;
    this.channel = channel;
    this.vaildCount = vaildCount;
    this.systemTime = new Date();
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getMessageId() {
    return messageId;
  }
  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
  public String getChannel() {
    return channel;
  }
  public void setChannel(String channel) {
    this.channel = channel;
  }
  public Long getVaildCount() {
    return vaildCount;
  }
  public void setVaildCount(Long vaildCount) {
    this.vaildCount = vaildCount;
  }
  public Date getSystemTime() {
    return systemTime;
  }
  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }

}
