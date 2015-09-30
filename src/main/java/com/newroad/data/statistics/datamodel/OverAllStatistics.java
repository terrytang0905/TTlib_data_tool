package com.newroad.data.statistics.datamodel;

import java.io.Serializable;
import java.util.Date;

public class OverAllStatistics implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4616095275872867091L;

  private Integer id;
  private Long activateUserNum;
  private Long registerUserNum;
  private Long offlineUserNum;
  private Long loginUserNum;
  private Long syncUserNum;
  private Long allNoteCount;
  private Long allResourceCount;
  private Integer taskCount;
  private Long executeTime;
  private Date statisticsTime;
  private Date systemTime;

  public OverAllStatistics(Long activateUserNum, Long registerUserNum, Long offlineUserNum, Long loginUserNum, Long syncUserNum,
      Long allNoteCount, Long allResourceCount, Long executeTime,Long statisticsTime) {
    super();
    this.activateUserNum = activateUserNum;
    this.registerUserNum = registerUserNum;
    this.offlineUserNum = offlineUserNum;
    this.loginUserNum = loginUserNum;
    this.syncUserNum = syncUserNum;
    this.allNoteCount = allNoteCount;
    this.allResourceCount = allResourceCount;
    this.taskCount = +1;
    this.executeTime = executeTime;
    this.statisticsTime = new Date(statisticsTime);
    this.systemTime = new Date(System.currentTimeMillis());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Long getActivateUserNum() {
    return activateUserNum;
  }

  public void setActivateUserNum(Long activateUserNum) {
    this.activateUserNum = activateUserNum;
  }

  public Long getRegisterUserNum() {
    return registerUserNum;
  }

  public void setRegisterUserNum(Long registerUserNum) {
    this.registerUserNum = registerUserNum;
  }

  public Long getOfflineUserNum() {
    return offlineUserNum;
  }

  public void setOfflineUserNum(Long offlineUserNum) {
    this.offlineUserNum = offlineUserNum;
  }

  public Long getLoginUserNum() {
    return loginUserNum;
  }

  public void setLoginUserNum(Long loginUserNum) {
    this.loginUserNum = loginUserNum;
  }

  public Long getSyncUserNum() {
    return syncUserNum;
  }

  public void setSyncUserNum(Long syncUserNum) {
    this.syncUserNum = syncUserNum;
  }

  public Long getAllNoteCount() {
    return allNoteCount;
  }

  public void setAllNoteCount(Long allNoteCount) {
    this.allNoteCount = allNoteCount;
  }

  public Long getAllResourceCount() {
    return allResourceCount;
  }

  public void setAllResourceCount(Long allResourceCount) {
    this.allResourceCount = allResourceCount;
  }

  public Integer getTaskCount() {
    return taskCount;
  }

  public void setTaskCount(Integer taskCount) {
    this.taskCount = taskCount;
  }


  public Long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Long executeTime) {
    this.executeTime = executeTime;
  }

  public Date getStatisticsTime() {
    return statisticsTime;
  }

  public void setStatisticsTime(Date statisticsTime) {
    this.statisticsTime = statisticsTime;
  }

  public Date getSystemTime() {
    return systemTime;
  }

  public void setSystemTime(Date systemTime) {
    this.systemTime = systemTime;
  }
  
}
