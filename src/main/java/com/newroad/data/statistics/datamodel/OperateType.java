package com.newroad.data.statistics.datamodel;

import java.io.Serializable;

/**
 * data operate type
 */
public enum OperateType implements Serializable{
  
  
  /**
   * 软件安装
   */
  install(0),
  /**
   * 软件激活
   */
  activate(1),
  /**
   * 软件卸载
   */
  uninstall(-1),
  /**
   * 用户注册
   */
  register(2),
  /**
   * 用户登录
   */
  login(3),
  /**
   * 用户登出
   */
  logout(4),
  /**
   * 打开软件
   */
  start(5),
  /**
   * 退出软件
   */
  stop(6),

  /**
   * 软件同步操作，未识别类型的同步数据
   */
  running(7),
  /**
   * name对应同步接口参数中的"createNotes",
   */
  createNotes(9),
  /**
   * name对应同步接口参数中的"updateNotes",
   */
  updateNotes(10),
  /**
   * name对应同步接口参数中的"deleteNotes",
   */
  deleteNotes(11),
  /**
   * name对应同步接口参数中的"createResources",
   */
  createResources(12),
  /**
   * name对应同步接口参数中的"updateResources",
   */
  updateResources(13),
  /**
   * name对应同步接口参数中的"deleteResources",
   */
  deleteResources(14),
  /**
   * name对应同步接口参数中的"createCategories",
   */
  createCategories(15),
  /**
   * name对应同步接口参数中的"updateCategories",
   */
  updateCategories(16),
  /**
   * name对应同步接口参数中的"deleteCategories",
   */
  deleteCategories(17),
  /**
   * name对应同步接口参数中的"createTags",
   */
  createTags(18),
  /**
   * name对应同步接口参数中的"updateTags",
   */
  updateTags(19),
  /**
   * name对应同步接口参数中的"deleteTags",
   */
  deleteTags(20),
  /**
   * 评分
   */
  score(21),
  /**
   * 同步
   */
  synchronize(22),
  /**
   * 提交
   */
  commit(23),
  /**
   * 离线统计
   */
  offline(24);

  private Integer value;

  private OperateType(Integer value) {
    this.value = value;
  }

  public static Integer getOperateType(String operateType) {
    return OperateType.valueOf(operateType).getValue();
  }
  
  public static Integer getOperateType(OperateType operateType) {
    return operateType.getValue();
  }

  public Integer getValue() {
    return value;
  }
}