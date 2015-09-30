package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

import com.newroad.data.common.utils.NoteUserStatisticsConstants;

import net.sf.json.JSONObject;

public class RegisterUserRecord implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -3944583960981504409L;
  private Integer id;
  private String deviceId;
  private String source;
  private String version;
  private String network;
  private String ip;

  private String userId;
  private String registerType;
  private Date registerTime;

  private Long userNoteCount;
  private Long userCategoryCount;
  private Long userTagCount;
  private Long userResourceCount;
  private Integer userLoginNum;
  private Integer userSyncNum;

  private Date lastLoginTime;
  private Date lastSyncTime;
  private Date systemTime;

  public RegisterUserRecord() {
    super();
    // TODO Auto-generated constructor stub
  }

  public RegisterUserRecord(String deviceId, String source, String version, String network, String ip, String userId, String registerType,
      Date registerTime, Long userNoteCount, Long userCategoryCount, Long userTagCount, Long userResourceCount, Integer userLoginNum,
      Integer userSyncNum, Date lastLoginTime, Long lastSyncTime) {
    super();
    this.deviceId = deviceId;
    this.source = source;
    this.version = version;
    this.network = network;
    this.ip = ip;

    this.userId = userId;
    this.registerType = registerType;
    this.registerTime = registerTime;
    this.userNoteCount = userNoteCount;
    this.userCategoryCount = userCategoryCount;
    this.userTagCount = userTagCount;
    this.userResourceCount = userResourceCount;
    this.userLoginNum = userLoginNum;
    this.userSyncNum = userSyncNum;
    this.lastLoginTime = lastLoginTime;
    if (lastSyncTime != null) {
      this.lastSyncTime = new Date(lastSyncTime);
    } else {
      this.lastSyncTime = null;
    }
    this.systemTime = new Date();
  }
  
  public RegisterUserRecord(JSONObject userStatistics, String userId, Long registerTime, Long userNoteCount, Long userCategoryCount, Long userTagCount,
      Long userResourceCount, Integer userSyncNum, Long lastSyncTime) {
    if (userStatistics.has("deviceID")) {
      this.deviceId = userStatistics.getString("deviceID");
    } else {
      this.deviceId = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("source")) {
      this.source = userStatistics.getString("source");
    } else {
      this.source = NoteUserStatisticsConstants.UNKNOWN;
    }
    this.version = userStatistics.getString("version");
    if (userStatistics.has("network")) {
      this.network = userStatistics.getString("network");
    } else {
      this.network = "";
    }
    this.ip = userStatistics.getString("ip");
    this.userId = userId;
    if (userStatistics.has("accessMethod")) {
      String accessMethod = userStatistics.getString("accessMethod");
      this.registerType = accessMethod;
    } else {
      this.registerType = "LenovoID";
    }

    this.registerTime = new Date(registerTime);
    this.userNoteCount = userNoteCount;
    this.userCategoryCount = userCategoryCount;
    this.userTagCount = userTagCount;
    this.userResourceCount = userResourceCount;
    this.userLoginNum = 0;
    this.userSyncNum = userSyncNum;
    this.lastLoginTime = null;
    if (lastSyncTime != null) {
      this.lastSyncTime = new Date(lastSyncTime);
    } else {
      this.lastSyncTime = null;
    }
    this.systemTime = new Date();
  }

  public RegisterUserRecord(JSONObject userStatistics) {
    if (userStatistics.has("deviceID")) {
      this.deviceId = userStatistics.getString("deviceID");
    } else {
      this.deviceId = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("source")) {
      this.source = userStatistics.getString("source");
    } else {
      this.source = NoteUserStatisticsConstants.UNKNOWN;
    }
    this.version = userStatistics.getString("version");
    if (userStatistics.has("network")) {
      this.network = userStatistics.getString("network");
    } else {
      this.network = "";
    }
    this.ip = userStatistics.getString("ip");
    if (userStatistics.has("userID")) {
      this.userId = userStatistics.getString("userID");
    } else {
      this.userId = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("accessMethod")) {
      String accessMethod = userStatistics.getString("accessMethod");
      this.registerType = accessMethod;
    } else {
      this.registerType = "LenovoID";
    }
    Long createTimeLong = userStatistics.getLong("createTime");
    if (createTimeLong != null) {
      this.registerTime = new Date(createTimeLong);
    } else {
      this.registerTime = null;
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

  public String getRegisterType() {
    return registerType;
  }

  public void setRegisterType(String registerType) {
    this.registerType = registerType;
  }

  public Date getRegisterTime() {
    return registerTime;
  }

  public void setRegisterTime(Date registerTime) {
    this.registerTime = registerTime;
  }

  public void setRegisterTime(Long timeMs) {
    if (timeMs != null) {
      this.registerTime = new Date(timeMs);
    } else {
      this.registerTime = null;
    }
  }

  public Long getUserNoteCount() {
    return userNoteCount;
  }

  public void setUserNoteCount(Long userNoteCount) {
    this.userNoteCount = userNoteCount;
  }

  public Long getUserCategoryCount() {
    return userCategoryCount;
  }

  public void setUserCategoryCount(Long userCategoryCount) {
    this.userCategoryCount = userCategoryCount;
  }

  public Long getUserTagCount() {
    return userTagCount;
  }

  public void setUserTagCount(Long userTagCount) {
    this.userTagCount = userTagCount;
  }

  public Long getUserResourceCount() {
    return userResourceCount;
  }

  public void setUserResourceCount(Long userResourceCount) {
    this.userResourceCount = userResourceCount;
  }

  public Integer getUserLoginNum() {
    return userLoginNum;
  }

  public void setUserLoginNum(Integer userLoginNum) {
    this.userLoginNum = userLoginNum;
  }

  public Integer getUserSyncNum() {
    return userSyncNum;
  }

  public void setUserSyncNum(Integer userSyncNum) {
    this.userSyncNum = userSyncNum;
  }

  public Date getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(Date lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }

  public Date getLastSyncTime() {
    return lastSyncTime;
  }

  public void setLastSyncTime(Date lastSyncTime) {
    this.lastSyncTime = lastSyncTime;
  }

  public void setLastSyncTime(Long timeMs) {
    if (timeMs != null) {
      this.lastSyncTime = new Date(timeMs);
    } else {
      this.lastSyncTime = null;
    }
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }

}
