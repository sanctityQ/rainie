package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDao extends PagingAndSortingRepository<Activity,Long> {


    /**
     * 根据tx_id,status查询
     * @param txId
     * @return
     */
    Activity findByTxId(String txId);

    /**
     * 修改主事务状态
     * @param actionId
     * @param afterStatus
     * @param beforeStatus
     * @return
     */
    @Modifying
    @Query("update Activity a set a.status =?2 where a.txId = ?1 and a.status = ?3 and a.finish = 0")
    int updateAcvityStatus(String txId, int afterStatus, int beforeStatus);

    /**
     * 修改主事务状态及finish
     * @param actionId
     * @param afterStatus
     * @param beforeStatus
     * @return
     */
    @Modifying
    @Query("update Activity a set a.finish = 1 where a.txId = ?1 and a.status = ?2 and a.finish = 0")
    int updateActivityFinish(String txId,int status);

}
