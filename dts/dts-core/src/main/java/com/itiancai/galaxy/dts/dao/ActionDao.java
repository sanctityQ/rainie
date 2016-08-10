package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Action;

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
    Action findByActionId(String actionId);

    /**
     * 根据actionId查询Action记录
     * @param txId 子事务id
     * @return Action
     */
    List<Action> findByTxId(String txId);

    /**
     * 修改子事务状态
     * @param actionId
     * @param status
     * @param preStatus
     * @return
     */
    @Modifying
    @Query("update Action set status=?2 where actionId=?1 and status=?3")
    int updateStatus(String actionId, int status, int preStatus);

}
