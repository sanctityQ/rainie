package com.itiancai.galaxy.dts.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lsp on 16/7/28.
 */
@Entity
@Table(name = "dts_activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 主事务id
     */
    @Column(name = "tx_id", nullable = false)
    private String txId;

    /**
     * 业务为号
     */
    @Column(name = "business_id", nullable = false)
    private String businessId;

    /**
     * 主事务状态(UNKNOWN,SUCCESS,FAIL)
     */
    @Column(name = "status", nullable = false)
    private int status;

    /**
     * 服务名称
     */
    @Column(name = "business_type", nullable = false)
    private String businessType;

    /**
     * 事务完成标识 0-未完成,1-完成
     */
    @Column(name = "finish")
    private int finish;

    //收集标志 0-未收集 1-已收集
    @Column(name = "collect")
    private int collect;
    //重试次数
    @Column(name = "retry_count")
    private int retryCount;

    /**
     * 数据创建时间
     */
    @Column(name = "c_time")
    private Date cTime;

    /**
     * 数据修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "m_time")
    private Date mTime;

    /**
     * 超时时间 毫秒
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_out", nullable = false)
    private int timeOut;

    public Activity(){}

    public Activity(String txId, Status.Activity status, String businessType, Date cTime,int timeOut,
                    Date mTime,Integer finish, String businessId){
        this.txId = txId;
        this.status = status.getStatus();
        this.businessType = businessType;
        this.cTime = cTime;
        this.mTime = mTime;
        this.finish = finish;
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

    public Status.Activity getStatus() {
        return Status.Activity.getStatus(status);
    }

    public void setStatus(Status.Activity status) {
        this.status = status.getStatus();
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

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
