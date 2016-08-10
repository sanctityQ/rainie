package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Activity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lsp on 16/7/28.
 */
public interface ActivityDao extends PagingAndSortingRepository<Activity, Long> {

    /**
     * 根据tx_id,status查询
     * @param txId
     * @return
     */
    Activity findByTxId(String txId);

    /**
     * 更新状态
     * @param txId
     * @param status
     * @param preStatus
     * @return
     */
    @Modifying
    @Query("update Activity set status=?2 where txId=?1 and status=?3 and finish = 0")
    int updateStatus(String txId, int status, int preStatus);

    /**
     * 修改主事务状态及finish
     * @param txId
     * @param status
     * @return
     */
    @Modifying
    @Query("update Activity a set a.finish = 1 where a.txId = ?1 and a.status = ?2 and a.finish = 0")
    int updateActivityFinish(String txId,int status);

    /**
     * 修改完成标志
     * @param txId
     * @return
     */
    @Modifying
    @Query("update Activity set finish=1 where txId=?1 and finish = 0")
    int finishActivity(String txId);

    /**
     * 查询未完成事务(成功|失败)
     * @return
     */
    @Query(value = "select tx_id " +
            "from dts_activity dat " +
            "where dat.finish = 0 " +
            "and dat.collect = 0 " +
            "and dat.status in (2, 3) " +
            "and dat.retry_count < ?1 " +
            "order by dat.c_time " +
            "LIMIT 0, 499", nativeQuery = true)
    List<String> listSuccessOrFail(int maxRetryCount);

    /**
     * 查询未知超时事务
     * @return
     */
    @Query(value = "select tx_id " +
            "from dts_activity dat " +
            "where dat.finish = 0 " +
            "and dat.collect = 0 " +
            "and dat.status = 0 " +
            "and DATE_ADD(dat.c_time,INTERVAL dat.time_out/1000 SECOND) < now() " +
            "and dat.retry_count < ?1 " +
            "order by dat.c_time " +
            "LIMIT 0, 499", nativeQuery = true)
    List<String> listUnknownAndTimeout(int maxRetryCount);

    /**
     * 更新收集状态 //TODO retry_count
     * @param txId
     * @return
     */
    @Modifying
    @Query("update Activity set collect=1 where txId=?1 and collect=0 and finish = 0 and retry_count < ?2")
    int collect(String txId, int maxRetryCount);

    /**
     * 回收tx; 重试次数+1,未收集状态
     * @param txId
     * @return
     */
    @Modifying
    @Query("update Activity set collect=0, retry_count=retry_count+1 where txId=?1 and finish = 0")
    int reclaim(String txId);

    /**
     * 回收已收集tx,处理超时
     * @return
     */
    @Modifying
    @Query(value = "update dts_activity dat " +
            "set dat.collect = 0 " +
            "where dat.finish = 0 " +
            "and dat.collect = 1 " +
            "and DATE_ADD(dat.m_time,INTERVAL ?1 SECOND) < now() ", nativeQuery = true)
    int reclaimHandleTimeout(int handleTimeout);

}
