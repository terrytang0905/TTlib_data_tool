package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;

import net.sf.json.JSONObject;

public class UserActiveStatistics implements Serializable {



  /**
   * 
   */
  private static final long serialVersionUID = 1329496233302446760L;
  private Integer id;
  private String deviceId;
  private String source;
  private String version;
  private String network;
  private String ip;

  private Integer operateType;
  private String userId;
  private Date accessTime;
  private Date systemTime;

  public UserActiveStatistics(JSONObject userStatistics) {
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
    if (userStatistics.has("version")) {
      this.version = userStatistics.getString("version");
    } else {
      this.version = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("network")) {
      this.network = userStatistics.getString("network");
    } else {
      this.network = "";
    }
    this.ip = userStatistics.getString("ip");
    this.userId =(String) userStatistics.get("userID");
    this.operateType = OperateType.getOperateType(userStatistics.getString("operateType"));
    Long createTimeLong = userStatistics.getLong("createTime");
    if (createTimeLong != null) {
      this.accessTime = new Date(createTimeLong);
    } else {
      this.accessTime = null;
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

  public Integer getOperateType() {
    return operateType;
  }

  public void setOperateType(Integer operateType) {
    this.operateType = operateType;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Date getAccessTime() {
    return accessTime;
  }

  public void setAccessTime(Date accessTime) {
    this.accessTime = accessTime;
  }

  public void setAccessTime(Long timeMs) {
    if (timeMs != null) {
      this.accessTime = new Date(timeMs);
    } else {
      this.accessTime = null;
    }
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }


}
