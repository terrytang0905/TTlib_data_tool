package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

import com.newroad.data.common.utils.NoteUserStatisticsConstants;

import net.sf.json.JSONObject;

public class UserDayStatistics implements Serializable{
  
  
  /**
   * 
   */
  private static final long serialVersionUID = 6450419924074299061L;
  private Integer id;
  private String deviceId;
  private String source;
  private String version;
  private String network;
  private String ip;

  // if userId = null, user is offline user.
  private String userId;
  private Long noteCount;
  private Long categoryCount;
  private Long tagCount;
  private Long resourceCount;
  private Integer dayLoginNum;
  private Integer daySyncNum;
  private Date clientDataTime;
  private Date systemTime;

  
  
  public UserDayStatistics() {
    super();
    // TODO Auto-generated constructor stub
  }

  public UserDayStatistics(JSONObject userStatistics, Integer dayLoginNum, Integer daySyncNum) {
    this.deviceId = userStatistics.getString("deviceID");
    if (userStatistics.has("source")) {
      this.source = userStatistics.getString("source");
    } else {
      this.source = NoteUserStatisticsConstants.UNKNOWN;
    }
    this.version = userStatistics.getString("version");
    if (userStatistics.has("network")) {
      this.network = userStatistics.getString("network");
    }else{
      this.network = "";
    }
    this.ip = userStatistics.getString("ip");
    this.userId = userStatistics.getString("userID");
    this.noteCount = userStatistics.getLong("noteCount");
    this.categoryCount = userStatistics.getLong("categoryCount");
    this.tagCount = userStatistics.getLong("tagCount");
    this.resourceCount = userStatistics.getLong("resourceCount");
    this.dayLoginNum = dayLoginNum;
    this.daySyncNum = daySyncNum;
    Long createTimeLong = userStatistics.getLong("createTime");
    if (createTimeLong != null) {
      this.clientDataTime = new Date(createTimeLong);
    } else {
      this.clientDataTime = null;
    }
    this.systemTime = new Date();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
  
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
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

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Long getNoteCount() {
    return noteCount;
  }

  public void setNoteCount(Long noteCount) {
    this.noteCount = noteCount;
  }

  public Long getResourceCount() {
    return resourceCount;
  }

  public void setResourceCount(Long resourceCount) {
    this.resourceCount = resourceCount;
  }

  public Long getCategoryCount() {
    return categoryCount;
  }

  public void setCategoryCount(Long categoryCount) {
    this.categoryCount = categoryCount;
  }

  public Long getTagCount() {
    return tagCount;
  }

  public void setTagCount(Long tagCount) {
    this.tagCount = tagCount;
  }

  public Integer getDayLoginNum() {
    return dayLoginNum;
  }

  public void setDayLoginNum(Integer dayLoginNum) {
    this.dayLoginNum = dayLoginNum;
  }

  public Integer getDaySyncNum() {
    return daySyncNum;
  }

  public void setDaySyncNum(Integer daySyncNum) {
    this.daySyncNum = daySyncNum;
  }

  public Date getClientDataTime() {
    return clientDataTime;
  }
  
  public void setClientDataTime(Date clientDataTime) {
    this.clientDataTime = clientDataTime;
  }

  public void setClientDataTime(Long timeMs) {
    if (timeMs != null) {
      this.clientDataTime = new Date(timeMs);
    } else {
      this.clientDataTime = null;
    }
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }


}
