package com.itiancai.galaxy.dts.domain;

import java.util.Date;

public class Action {

  private Long id;

  /**
   * 主事务id
   */
  private String txId;

  /**
   * 幂等值
   */
  private String instructionId;

  /**
   * 子事务id
   */
  private String actionId;

  /**
   * 子事务状态(UNKNOWN,PREPARE,SUCCESS,FAIL)
   */
  private int status;

  /**
   * 服务名称
   */
  private String serviceName;

  /**
   * 数据创建时间
   */
  private Date cTime;

  /**
   * 数据修改时间
   */
  private Date mTime;

  public Action() {
  }

  public Action(String txId, String actionId, Status.Action status, String serviceName, String instructionId) {
    this.txId = txId;
    this.actionId = actionId;
    this.status = status.getStatus();
    this.serviceName = serviceName;
    this.instructionId = instructionId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public String getInstructionId() {
    return instructionId;
  }

  public void setInstructionId(String instructionId) {
    this.instructionId = instructionId;
  }

  public String getActionId() {
    return actionId;
  }

  public void setActionId(String actionId) {
    this.actionId = actionId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Date getcTime() {
    return cTime;
  }

  public void setcTime(Date cTime) {
    this.cTime = cTime;
  }

  public Date getmTime() {
    return mTime;
  }

  public void setmTime(Date mTime) {
    this.mTime = mTime;
  }
}
