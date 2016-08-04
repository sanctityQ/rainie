package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Action;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionDao extends PagingAndSortingRepository<Action,Long> {

    /**
     * 根据actionId查询Action记录
     * @param actionId 子事务id
     * @return Action
     */
    @Query("select a from Action a where a.actionId = ?1 ")
    Action findActionByActionId(long actionId);


    /**
     * 根据actionId查询Action记录
     * @param txId 子事务id
     * @return Action
     */
    @Query("select a from Action a where a.txId = ?1 ")
    List<Action> findActionByTxId(long txId);

}
