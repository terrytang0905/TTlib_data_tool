package com.newroad.data.common.utils;

public class NoteUserStatisticsConstants {
  
  public final static String COLLECTION_STATISTICS = "ln_user_statistics";
  
  public final static String COLLECTION_USER = "ln_user";
  
  public final static String COLLECTION_NOTE = "ln_note";

  public final static String COLLECTION_CATE = "ln_category";

  public final static String COLLECTION_TAG = "ln_tag";

  public final static String COLLECTION_RESOURCE = "ln_resource";

  public final static String COLLECTION_OPLOG = "ln_oplog";


  public static final String DEVICE_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.DeviceStatisticsMapper";

  public static final String OVERALL_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.OverAllStatisticsMapper";

  public static final String REGISTER_USER_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.RegisterUserStatisticsMapper";

  public static final String USER_DAYSTATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.UserDayStatisticsMapper";

  public static final String USER_ACTIVE_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.UserActiveStatisticsMapper";

  public static final String USER_LOGIN_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.UserLoginStatisticsMapper";

  public static final String USER_SYNC_STATISTICS_MAPPER = "com.lenovo.data.statistics.sqlmapper.UserSyncStatisticsMapper";
  

  public final static String SELECT_DEVICE_RECORD = DEVICE_STATISTICS_MAPPER + ".selectDeviceRecord";

  public final static String INSERT_DEVICE_RECORD = DEVICE_STATISTICS_MAPPER + ".insertDeviceRecord";

  public final static String UPDATE_DEVICE_RECORD_INITIAL_TIME = DEVICE_STATISTICS_MAPPER + ".updateDeviceRecordInitialTime";

  public final static String UPDATE_ACTIVE_DEVICE_USABLE = DEVICE_STATISTICS_MAPPER + ".updateActiveDeviceUsable";
  

  public final static String SELECT_OVERALL_STATISTICS = OVERALL_STATISTICS_MAPPER + ".selectOverAllStatistics";
  
  public final static String GET_MAX_OVERALL_STATISTICS = OVERALL_STATISTICS_MAPPER + ".getMaxOverAllStatistics";
  
  public final static String INSERT_OVERALL_STATISTICS = OVERALL_STATISTICS_MAPPER + ".insertOverAllStatistics";
  
  
  public final static String SELECT_REGISTER_USER_RECORD = REGISTER_USER_STATISTICS_MAPPER + ".selectRegisterUserRecord";
  
  public final static String SELECT_REGISTER_USER_RECORD_BY_DEVICE = REGISTER_USER_STATISTICS_MAPPER + ".selectRegisterUserRecordByDevice";
  
  public final static String SELECT_REGISTER_USER_RECORD_BY_USER = REGISTER_USER_STATISTICS_MAPPER + ".selectRegisterUserRecordByUser";
  
  public final static String INSERT_REGISTER_USER_RECORD = REGISTER_USER_STATISTICS_MAPPER + ".insertRegisterUserRecord";

  public final static String COUNT_REGISTER_USER_RECORD = REGISTER_USER_STATISTICS_MAPPER + ".countRegisterUserRecord";
  
  public final static String UPDATE_REGUSER_STATISTICS = REGISTER_USER_STATISTICS_MAPPER + ".updateRegUserStatistics";

  public final static String UPDATE_REGUSER_LOGIN_STATISTICS = REGISTER_USER_STATISTICS_MAPPER + ".updateRegUserLoginStatistics";
  
  public final static String UPDATE_REGUSER_SYNC_STATISTICS = REGISTER_USER_STATISTICS_MAPPER + ".updateRegUserSyncStatistics";


  public final static String SELECT_USER_DAY_STATISTICS = USER_DAYSTATISTICS_MAPPER + ".selectUserDayStatistics";  
  
  public final static String COUNT_USER_DAY_STATISTICS = USER_DAYSTATISTICS_MAPPER + ".countUserDayStatistics";
  
  public final static String INSERT_USER_DAY_STATISTICS = USER_DAYSTATISTICS_MAPPER + ".insertUserDayStatistics";

  public final static String UPDATE_DAY_USER_NUM = USER_DAYSTATISTICS_MAPPER + ".updateDayUserNum";

  
  public final static String SELECT_USER_ACTIVE_STATISTICS = USER_ACTIVE_STATISTICS_MAPPER + ".selectUserActiveStatistics";
  
  public final static String INSERT_USER_ACTIVE_RECORD = USER_ACTIVE_STATISTICS_MAPPER + ".insertUserActiveStatistics";

  public final static String COUNT_USER_ACTIVE_RECORD = USER_ACTIVE_STATISTICS_MAPPER + ".countUserActiveRecord";
  
  
  public final static String INSERT_USER_LOGIN_STATISTICS = USER_LOGIN_STATISTICS_MAPPER + ".insertUserLoginStatistics";

  public final static String SELECT_USER_LOGIN_STATISTICS = USER_LOGIN_STATISTICS_MAPPER + ".selectUserLoginStatistics";

  public final static String GET_LAST_USER_LOGIN_TIME = USER_LOGIN_STATISTICS_MAPPER + ".getUserLastLoginTime";
  
  public final static String GETT_LOGIN_USER_LIST = USER_LOGIN_STATISTICS_MAPPER + ".getLoginUserList";

  public final static String COUNT_USER_LOGIN_RECORD = USER_LOGIN_STATISTICS_MAPPER + ".countUserLoginRecord";
  
  public final static String COUNT_LOGIN_USER_NUM = USER_LOGIN_STATISTICS_MAPPER + ".countLoginUserNum";

  
  public final static String INSERT_USER_SYNC_STATISTICS = USER_SYNC_STATISTICS_MAPPER + ".insertUserSyncStatistics";

  public final static String SELECT_USER_SYNC_STATISTICS = USER_SYNC_STATISTICS_MAPPER + ".selectUserSyncStatistics";

  public final static String GET_LAST_USER_SYNC_TIME = USER_SYNC_STATISTICS_MAPPER + ".getUserLastSyncTime";
  
  public final static String GET_SYNC_USER_LIST = USER_SYNC_STATISTICS_MAPPER+".getSyncUserList";

  public final static String COUNT_USER_SYNC_RECORD = USER_SYNC_STATISTICS_MAPPER + ".countUserSyncRecord";
  
  public final static String UNKNOWN="unknown";

}
