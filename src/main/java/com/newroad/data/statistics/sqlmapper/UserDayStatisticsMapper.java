package com.newroad.data.statistics.sqlmapper;

import com.newroad.data.statistics.datamodel.UserDayStatistics;

public interface UserDayStatisticsMapper {
  
  void insertUserDayStatistics(UserDayStatistics userDayStatistics);
  
  Long countUserDayStatistics(UserDayStatistics userDayStatistics);
  
  void updateUserDayStatistics(UserDayStatistics userDayStatistics);
  
}
