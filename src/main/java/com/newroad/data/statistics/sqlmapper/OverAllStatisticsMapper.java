package com.newroad.data.statistics.sqlmapper;

import java.util.List;

import com.newroad.data.statistics.datamodel.OverAllStatistics;

public interface OverAllStatisticsMapper {
  
  List<OverAllStatistics> selectOverAllStatistics(OverAllStatistics overAllStatistics);
  
  OverAllStatistics getMaxOverAllStatistics(OverAllStatistics overAllStatistics);
  
  void insertOverAllStatistics(OverAllStatistics overAllStatistics);
}
