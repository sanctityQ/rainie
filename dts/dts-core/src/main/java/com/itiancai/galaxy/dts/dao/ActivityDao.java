package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.domain.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDao extends PagingAndSortingRepository<Activity,Long> {


    /**
     * 根据tx_id,status查询
     * @param txId
     * @return
     */
    @Query("select a from Activity a where a.txId = ?1 ")
    Activity findActivityByTxId(long txId);

}
