package com.itiancai.galaxy.dts.store.impl;

import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.Status;
import com.itiancai.galaxy.dts.store.ActivityDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;

@Repository
public class ActivityDaoImpl implements ActivityDao {

  private final static Logger logger = LoggerFactory.getLogger(ActivityDaoImpl.class);

  @Resource(name = "dtsJdbcTemplate")
  private JdbcTemplate jdbcTemplate;

  @Override
  public Activity findByTxId(String txId) {
    String sql = "select id, tx_id txId, business_id businessId, " +
            "status status, business_type businessType, finish, " +
            "retry_count retryCount, time_out timeOut, c_time cTime,m_time mTime " +
            "from dts_activity where tx_id=?";

    return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Activity.class), txId);
  }

  @Override
  public int updateStatusByTxIdStatus(String txId, int status, int preStatus) {
    String sql = "update dts_activity set status=? where tx_id=? and status=? and finish = 0";
    return jdbcTemplate.update(sql, status, txId, preStatus);
  }

  @Override
  public int finishActivity(String txId) {
    String sql = "update dts_activity set finish=1 where tx_id=? and finish = 2";
    return jdbcTemplate.update(sql, txId);
  }

  @Override
  public List<String> listSuccessOrFail(int index, int total, int maxRetryCount) {
    String sql = "select tx_id " +
            "from dts_activity dat " +
            "where dat.finish = 0 " +
            "and dat.status in (2, 3) " +
            "and dat.id % ? = ? " +
            "and dat.retry_count < ? " +
            "order by dat.c_time " +
            "LIMIT 0, 199";
    return jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(), total, index, maxRetryCount);
  }

  @Override
  public List<String> listUnknownAndTimeout(int index, int total, int maxRetryCount) {
    String sql = "select tx_id " +
            "from dts_activity dat " +
            "where dat.finish = 0 " +
            "and dat.status = 0 " +
            "and dat.id % ? = ? " +
            "and DATE_ADD(dat.c_time,INTERVAL dat.time_out/1000 SECOND) < now() " +
            "and dat.retry_count < ? " +
            "order by dat.c_time " +
            "LIMIT 0, 199";
    return jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(), total, index, maxRetryCount);
  }


  @Override
  public int lockTXByTxIdAndStatus(String txId, Status.Activity status, int maxRetryCount) {
    String sql = "update dts_activity set finish=2, retry_count=retry_count+1 where tx_id=? and status = ?  and finish = 0 and retry_count < ?";
    return jdbcTemplate.update(sql, txId, status.getStatus(), maxRetryCount);
  }

  @Override
  public int reclaim(String txId) {
    String sql = "update dts_activity set finish=0 where tx_id=? and finish = 2";
    return jdbcTemplate.update(sql, txId);
  }

  @Override
  public int reclaimHandleTimeout(int handleTimeout) {
    String sql = "update dts_activity dat " +
              "set dat.finish = 0 " +
              "where dat.finish = 2 " +
              "and DATE_ADD(dat.m_time,INTERVAL ? SECOND) < now() ";
    return jdbcTemplate.update(sql, handleTimeout);
  }

  @Override
  public void save(Activity entity) {
    String sql = "INSERT INTO dts_activity (tx_id, business_id, business_type, time_out, status) " +
            "VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, entity.getTxId(), entity.getBusinessId(), entity.getBusinessType(), entity.getTimeOut(), entity.getStatus());
  }

  @Override
  public int incrementRetryCountByTxId(String txId) {
    String sql = "update dts_activity set retry_count=retry_count+1 where tx_id=? and finish = 0";
    return jdbcTemplate.update(sql, txId);
  }
}
