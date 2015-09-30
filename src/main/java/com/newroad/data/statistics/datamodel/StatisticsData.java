package com.newroad.data.statistics.datamodel;

import java.util.Date;

/**
 * SuperNote statistics data
 * 
 * @author: tangzj
 * @data : 2014-6-4
 * @since : 1.5
 */
public class StatisticsData {

  private String statsID;
  private String version;
  private String network;
  private String cc;
  private Source source;
  private String channel;
  private String ip;
  private String userID;
  private String deviceID;
  private String deviceType;
  private Long createTime;

  private UserFeedBack feedback;

  private OperateType operateType;
  private String operateDataID;

  /**
   * Data type is date from createTime.
   */
  private Date createDate;

  /**
   * device source
   */
  public static enum Source {
    androidPhone, androidPad, iosPhone, iosTouch, iosPad, web, wap, winPhone, winPad, winPC, winPCSimplified, webGt;
  }

  public String getStatsID() {
    return statsID;
  }

  public void setStatsID(String statsID) {
    this.statsID = statsID;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getCc() {
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Date getOptTime() {
    if (this.createTime == null) {
      return new Date();
    }
    return new Date(createTime);
  }

  public UserFeedBack getFeedback() {
    return feedback;
  }

  public void setFeedback(UserFeedBack feedback) {
    this.feedback = feedback;
  }

  public OperateType getOperateType() {
    return operateType;
  }

  public void setOperateType(OperateType operateType) {
    this.operateType = operateType;
  }

  public String getOperateDataID() {
    return operateDataID;
  }

  public void setOperateDataID(String operateDataID) {
    this.operateDataID = operateDataID;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Long timeMs) {
    if (timeMs != null) {
      this.createDate = new Date(timeMs);
    }
    this.createDate = new Date();
  }
}
