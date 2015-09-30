package com.newroad.data.statistics.sqlmapper;

import com.newroad.data.statistics.datamodel.UserActiveStatistics;

public interface UserActiveStatisticsMapper {

  UserActiveStatistics selectUserActiveStatistics(UserActiveStatistics userActiveStatistics);
  
  void insertUserActiveStatistics(UserActiveStatistics userActiveStatistics);

  Integer countActiveUserNum(UserActiveStatistics userActiveStatistics);

  //void updateUserActiveStatistics(UserActiveStatistics userActiveStatistics);
}
