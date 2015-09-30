package com.newroad.data.statistics.datamodel.message;

import java.util.Date;

public class MessageDeviceTypeStatistics {

  private String id;
  private String messageId;
  private String deviceType;
  private Long vaildCount;
  private Date systemTime;
  
  public MessageDeviceTypeStatistics(String messageId, String deviceType, Long vaildCount) {
    super();
    this.messageId = messageId;
    this.deviceType = deviceType;
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
  public String getDeviceType() {
    return deviceType;
  }
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
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
