package com.newroad.data.statistics.sqlmapper;

import java.util.List;

import com.newroad.data.statistics.datamodel.message.MessageChannelStatistics;
import com.newroad.data.statistics.datamodel.message.MessageDeviceTypeStatistics;
import com.newroad.data.statistics.datamodel.message.MessageViewStatistics;

public interface MessageStatisticsMapper {
  
  List<MessageViewStatistics> selectMsgVersionStatistics(MessageViewStatistics viewStatistics);
  
  void insertMsgVersionStatistics(MessageViewStatistics viewStatistics);

  void updateMsgVersionStatistics(MessageViewStatistics viewStatistics);
  
  List<MessageChannelStatistics> selectMsgChannelStatistics(MessageChannelStatistics channelStatistics);
  
  void insertMsgChannelStatistics(MessageChannelStatistics channelStatistics);

  void updateMsgChannelStatistics(MessageChannelStatistics channelStatistics);
  
  List<MessageDeviceTypeStatistics> selectMsgDeviceStatistics(MessageDeviceTypeStatistics deviceTypeStatistics);
  
  void insertMsgDeviceStatistics(MessageDeviceTypeStatistics deviceTypeStatistics);

  void updateMsgDeviceStatistics(MessageDeviceTypeStatistics deviceTypeStatistics);
}
