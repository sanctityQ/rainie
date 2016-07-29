package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.Status;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lsp on 16/7/28.
 */
public interface ActionDao extends PagingAndSortingRepository<Action,Long> {

    /**
     * 根据actionId查询Action记录
     * @param actionId 子事务id
     * @return Action
     */
    @Query("select a from Action a where a.actionId = ?1 ")
    Action listActionByActionId(long actionId);


    /**
     * 根据actionId查询Action记录
     * @param actionId 子事务id
     * @return Action
     */
    @Query("select a from Action a where a.txId = ?1 ")
    List<Action> listActionByTxId(long txId);

}
