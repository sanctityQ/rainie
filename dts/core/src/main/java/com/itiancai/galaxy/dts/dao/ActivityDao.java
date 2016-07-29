package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lsp on 16/7/28.
 */
public interface ActivityDao extends PagingAndSortingRepository<Activity,Long> {


    /**
     * 根据tx_id,status查询
     * @param txId
     * @return
     */
    @Query("select a from Activity a where a.txId = ?1 ")
    Activity findActivityByTxIdStatus(long txId);

}
