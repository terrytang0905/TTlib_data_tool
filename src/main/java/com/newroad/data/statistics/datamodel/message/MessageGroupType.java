package com.newroad.data.statistics.datamodel.message;


public enum MessageGroupType {
  VERSION("version",MessageViewStatistics.class), CHANNEL("channel",MessageChannelStatistics.class), DEVICE_TYPE("deviceType",MessageDeviceTypeStatistics.class);

  private String groupType;
  private Class<?> clazz;

  MessageGroupType(String groupType,Class<?> clazz) {
    this.groupType = groupType;
    this.clazz=clazz;
  }

  public static MessageGroupType getGroupType(String groupType) {
    return MessageGroupType.valueOf(groupType);
  }

  public String getGroupType() {
    return groupType;
  }

  public Class<?> getClazz() {
    return clazz;
  }
  
}
