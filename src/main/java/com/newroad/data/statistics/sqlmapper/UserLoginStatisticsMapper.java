package com.newroad.data.statistics.sqlmapper;

import java.util.List;

import com.newroad.data.statistics.datamodel.UserLoginStatistics;

public interface UserLoginStatisticsMapper {

  List<UserLoginStatistics> selectUserLoginRecord(UserLoginStatistics userLoginStatistics);

  void insertUserLoginStatistics(UserLoginStatistics userLoginStatistics);

  UserLoginStatistics getUserLastLoginTime(UserLoginStatistics userLoginStatistics);

  List<String> getLoginUserList(UserLoginStatistics userLoginStatistics);

  Integer countLoginUserNum(UserLoginStatistics userLoginStatistics);
}
