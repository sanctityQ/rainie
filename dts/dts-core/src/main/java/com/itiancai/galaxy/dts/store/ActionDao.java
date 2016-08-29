package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.domain.Action;

import java.util.List;

public interface ActionDao {

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
    List<Action> listByTxId(String txId);

    /**
     * 修改子事务状态
     * @param actionId
     * @param status
     * @param preStatus
     * @return
     */
//    @Modifying
//    @Transactional(value = "dtsTransactionManager")
//    @Query("update Action set status=?2 where actionId=?1 and status=?3")
    int updateStatus(String actionId, int status, int preStatus);

    void save(Action action);

}
