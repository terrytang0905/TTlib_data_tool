package com.newroad.data.statistics.sqlmapper;

import java.util.List;

import com.newroad.data.statistics.datamodel.UserSyncStatistics;

public interface UserSyncStatisticsMapper {
  
  List<UserSyncStatistics> selectUserSyncStatistics(UserSyncStatistics  userSyncStatistics);
  
  void insertUserSyncStatistics(UserSyncStatistics  userSyncStatistics);
  
  UserSyncStatistics getUserLastSyncTime(UserSyncStatistics  userSyncStatistics);
  
  List<String> getSyncUserList(UserSyncStatistics  userSyncStatistics);
  
  Integer countSyncUserSyncNum(UserSyncStatistics  userSyncStatistics);
}
