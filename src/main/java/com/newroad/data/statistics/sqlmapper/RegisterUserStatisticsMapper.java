package com.newroad.data.statistics.sqlmapper;

import com.newroad.data.statistics.datamodel.RegisterUserRecord;

public interface RegisterUserStatisticsMapper {

  RegisterUserRecord selectRegisterUserRecordByDevice(RegisterUserRecord registerUserRecord);
  
  RegisterUserRecord selectRegisterUserRecordByUser(RegisterUserRecord registerUserRecord);
  
  void insertRegisterUserRecord(RegisterUserRecord registerUserRecord);

  Integer countRegisterUserRecord(RegisterUserRecord registerUserRecord);
  
  void updateRegUserStatistics(RegisterUserRecord registerUserRecord);

  void updateRegUserLoginStatistics(RegisterUserRecord registerUserRecord);
  
  void updateRegUserSyncStatistics(RegisterUserRecord registerUserRecord);
}
