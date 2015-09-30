package com.newroad.data.statistics.sqlmapper;

import java.util.List;

import com.newroad.data.statistics.datamodel.DeviceRecord;

public interface DeviceStatisticsMapper {

  List<DeviceRecord> selectDeviceRecord(DeviceRecord deviceRecord);
  
  void insertDeviceRecord(DeviceRecord deviceRecord);

  void updateDeviceRecord(DeviceRecord deviceRecord);

  void updateLastDeviceRecord(DeviceRecord deviceRecord);
}
