package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;

import net.sf.json.JSONObject;

public class UserLoginStatistics implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 233932275523877443L;
  private Integer id;
  private String deviceId;
  private String source;
  private String version;
  private String network;
  private String ip;

  // login or logout
  private Integer operateType;
  private String userId;
  private String loginType;
  private Date loginTime;
  private Date systemTime;

  public UserLoginStatistics() {
    super();
    // TODO Auto-generated constructor stub
  }

  public UserLoginStatistics(JSONObject userStatistics) {
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
    this.operateType = OperateType.getOperateType(userStatistics.getString("operateType"));
    if (userStatistics.has("accessMethod")) {
      String accessMethod = userStatistics.getString("accessMethod");
      this.loginType = accessMethod;
    } else {
      this.loginType = "LenovoID";
    }
    Long createTimeLong = userStatistics.getLong("createTime");
    if (createTimeLong != null) {
      this.loginTime = new Date(createTimeLong);
    } else {
      this.loginTime = null;
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

  public String getLoginType() {
    return loginType;
  }

  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }

  public Date getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Date loginTime) {
    this.loginTime = loginTime;
  }

  public void setLoginTime(Long timeMs) {
    if (timeMs != null) {
      this.loginTime = new Date(timeMs);
    } else {
      this.loginTime = null;
    }
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }


}
