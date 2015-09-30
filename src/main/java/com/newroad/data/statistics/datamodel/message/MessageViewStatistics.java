package com.newroad.data.statistics.datamodel.message;

import java.util.Date;

public class MessageViewStatistics {

  private String id;
  private String messageId;
  private String version;
  private Long vaildCount;
  private Date systemTime;
  
  
  public MessageViewStatistics(String messageId, String version, Long vaildCount) {
    super();
    this.messageId = messageId;
    this.version = version;
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
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
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
