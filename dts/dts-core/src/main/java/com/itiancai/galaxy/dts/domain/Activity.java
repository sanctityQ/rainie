package com.itiancai.galaxy.dts.domain;

import java.util.Date;

public class Activity {

  private Long id;

  /**
   * 主事务id
   */
  private String txId;

  /**
   * 业务为号
   */
  private String businessId;

  /**
   * 主事务状态(UNKNOWN,SUCCESS,FAIL)
   */
  private int status;

  /**
   * 服务名称
   */
  private String businessType;

  /**
   * 事务完成标识 0-未完成,1-完成 2-处理中
   */
  private int finish;

  //重试次数
  private int retryCount;

  /**
   * 数据创建时间
   */
  private Date cTime;

  /**
   * 数据修改时间
   */
  private Date mTime;

  /**
   * 超时时间 毫秒
   */
  private int timeOut;

  public Activity() {
  }

  public Activity(String txId, Status.Activity status, String businessType, int timeOut, String businessId) {
    this.txId = txId;
    this.status = status.getStatus();
    this.businessType = businessType;
    this.businessId = businessId;
    this.timeOut = timeOut;
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

  public String getBusinessId() {
    return businessId;
  }

  public void setBusinessId(String businessId) {
    this.businessId = businessId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getBusinessType() {
    return businessType;
  }

  public void setBusinessType(String businessType) {
    this.businessType = businessType;
  }

  public int getFinish() {
    return finish;
  }

  public void setFinish(int finish) {
    this.finish = finish;
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

  public int getTimeOut() {
    return timeOut;
  }

  public void setTimeOut(int timeOut) {
    this.timeOut = timeOut;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }
}
