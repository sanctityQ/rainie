package com.itiancai.galaxy.dts.store.impl;

import com.itiancai.galaxy.dts.store.ActivityDao;
import com.itiancai.galaxy.dts.domain.Activity;

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
    try {
      return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Activity.class), txId);
    } catch (Throwable t) {
      logger.error("findByTxId error:"+txId, t);
      return null;
    }
  }

  @Override
  public int updateStatus(String txId, int status, int preStatus) {
    String sql = "update dts_activity set status=? where tx_id=? and status=? and finish = 0";
    try {
      return jdbcTemplate.update(sql, status, txId, preStatus);
    } catch (Throwable t) {
      logger.error("updateStatus error:"+txId, t);
      return 0;
    }
  }

  @Override
  public int finishActivity(String txId) {
    String sql = "update dts_activity set finish=1 where tx_id=? and finish = 2";
    try {
      return jdbcTemplate.update(sql, txId);
    } catch (Throwable t) {
      logger.error("finishActivity error:"+txId, t);
      return 0;
    }
  }

  @Override
  public List<String> listSuccessOrFail(int index, int total, int maxRetryCount) {
    String sql = "select tx_id " +
            "from dts_activity dat " +
            "where dat.finish = 0 " +
            "and dat.status in (2, 3) " +
            "and dat.retry_count < ? " +
            "and dat.id % ? = ? " +
            "order by dat.c_time " +
            "LIMIT 0, 199";
    try {
      return jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(), total, index, maxRetryCount);
    } catch (Throwable t) {
      logger.error("listSuccessOrFail", t);
      return null;
    }
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
    try {
      return jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(), total, index, maxRetryCount);
    } catch (Throwable t) {
      logger.error("listSuccessOrFail", t);
      return null;
    }
  }

  @Override
  public int handle(String txId, int maxRetryCount) {
    String sql = "update dts_activity set finish=2, retry_count=retry_count+1 where tx_id=? and finish = 0 and retry_count < ?";
    try {
      return jdbcTemplate.update(sql, txId, maxRetryCount);
    } catch (Throwable t) {
      logger.error("handleTX error:"+txId, t);
      return 0;
    }
  }

  @Override
  public int reclaim(String txId) {
    String sql = "update dts_activity set finish=0 where tx_id=? and finish = 2";
    try {
      return jdbcTemplate.update(sql, txId);
    } catch (Throwable t) {
      logger.error("reclaimTX error:"+txId, t);
      return 0;
    }
  }

  @Override
  public int reclaimHandleTimeout(int handleTimeout) {
    try {
      String sql = "update dts_activity dat " +
              "set dat.finish = 0 " +
              "where dat.finish = 2 " +
              "and DATE_ADD(dat.m_time,INTERVAL ? SECOND) < now() ";
      return jdbcTemplate.update(sql, handleTimeout);
    } catch (Throwable t) {
      logger.error("reclaimHandleTimeout error", t);
      return 0;
    }
  }

  @Override
  public void save(Activity entity) {
    try {
      String sql = "INSERT INTO dts_activity (tx_id, business_id, business_type, time_out, status) " +
              "VALUES (?, ?, ?, ?, ?)";
      jdbcTemplate.update(sql, entity.getTxId(), entity.getBusinessId(), entity.getBusinessType(), entity.getTimeOut(), entity.getStatus());
    } catch (Throwable t) {
      logger.error("findByTxId error:"+entity.getTxId(), t);
    }
  }
}
