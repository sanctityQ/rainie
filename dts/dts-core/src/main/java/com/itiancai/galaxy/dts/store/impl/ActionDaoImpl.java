package com.itiancai.galaxy.dts.store.impl;

import com.itiancai.galaxy.dts.store.ActionDao;
import com.itiancai.galaxy.dts.domain.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Repository
public class ActionDaoImpl implements ActionDao {

  private final static Logger logger = LoggerFactory.getLogger(ActionDaoImpl.class);

  @Resource(name = "dtsJdbcTemplate")
  private JdbcTemplate jdbcTemplate;


  @Override
  public Action findByActionId(String actionId) {
    String sql = "select id, tx_id txId, instruction_id instructionId, " +
            "action_id actionId, status, service_name serviceName, " +
            "c_time cTime, m_time mTime " +
            "from dts_action where action_id=?";
      return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Action.class), actionId);
  }

  @Override
  public List<Action> listByTxId(String txId) {
    String sql = "select id, tx_id txId, instruction_id instructionId, " +
            "action_id actionId, status, service_name serviceName, " +
            "c_time cTime, m_time mTime " +
            "from dts_action where tx_id=?";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Action.class), txId);
  }

  @Override
  public int updateStatusByIdStatus(String actionId, int status, int preStatus) {
    String sql = "update dts_action set status=? where action_id=? and status=?";
    return jdbcTemplate.update(sql, status, actionId, preStatus);
  }

  @Override
  public void save(Action action) {
    String sql = "INSERT INTO dts_action (tx_id, instruction_id, action_id, service_name, status) " +
            "VALUES (?,?,?,?,?)";
    jdbcTemplate.update(sql, action.getTxId(), action.getInstructionId(), action.getActionId(), action.getServiceName(), action.getStatus());
  }
}
