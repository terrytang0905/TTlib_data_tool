package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

import com.newroad.data.statistics.datamodel.OperateType;
import com.newroad.data.common.utils.NoteUserStatisticsConstants;

import net.sf.json.JSONObject;

public class DeviceRecord implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2406227362667294254L;

  private Integer id;
  private String deviceId;
  private String deviceType;
  private String cc;
  private String source;
  private String channel;
  private String version;
  private String network;
  private String ip;

  private Integer operateType;
  private Integer isUsable;

  private Date createTime;
  private Date systemTime;

  public DeviceRecord() {

  }

  public DeviceRecord(JSONObject userStatistics) {
    if (userStatistics.has("deviceID")) {
      this.deviceId = userStatistics.getString("deviceID");
    } else {
      this.deviceId = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("deviceType")) {
      this.deviceType = userStatistics.getString("deviceType");
    } else {
      this.deviceType = NoteUserStatisticsConstants.UNKNOWN;
    }
    if (userStatistics.has("source")) {
      this.source = userStatistics.getString("source");
    } else {
      this.source = NoteUserStatisticsConstants.UNKNOWN;
    }

    if (userStatistics.has("channel")) {
      this.channel = userStatistics.getString("channel");
    } else {
      this.channel = "";
    }

    if (userStatistics.has("version")) {
      this.version = userStatistics.getString("version");
    } else {
      this.version = "";
    }

    if (userStatistics.has("network")) {
      this.network = userStatistics.getString("network");
    } else {
      this.network = "";
    }

    if (userStatistics.has("cc")) {
      this.cc = userStatistics.getString("cc");
    } else {
      this.cc = "";
    }

    if (userStatistics.has("ip")) {
      this.ip = userStatistics.getString("ip");
    } else {
      this.ip = "";
    }

    this.operateType = OperateType.getOperateType(userStatistics.getString("operateType"));
    // The default value is 0.
    this.isUsable = 0;
    if (userStatistics.has("createTime")) {
      Long createTimeLong = userStatistics.getLong("createTime");
      if (createTimeLong != null) {
        this.createTime = new Date(createTimeLong);
      } else {
        this.createTime = null;
      }
    } else {
      this.createTime = null;
    }
    this.systemTime = new Date(System.currentTimeMillis());
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

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
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

  public Integer getIsUsable() {
    return isUsable;
  }

  public void setIsUsable(Integer isUsable) {
    this.isUsable = isUsable;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long timeMs) {
    if (timeMs != null) {
      this.createTime = new Date(timeMs);
    } else {
      this.createTime = new Date();
    }
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }


}
