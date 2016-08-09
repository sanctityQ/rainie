package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Action;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionDao extends PagingAndSortingRepository<Action,Long> {

    /**
     * 根据actionId查询Action记录
     * @param actionId 子事务id
     * @return Action
     */
    Action findByActionId(String actionId);

    /**
     * 修改子事务状态
     * @param actionId
     * @param afterStatus
     * @param beforeStatus
     * @return
     */
    @Modifying
    @Query("update Action a set a.status =?2 where a.actionId = ?1 and a.status = ?3")
    int updateActionStatus(String actionId,int afterStatus, int beforeStatus);


    /**
     * 根据actionId查询Action记录
     * @param txId 子事务id
     * @return Action
     */
    List<Action> findByTxId(String txId);

}
