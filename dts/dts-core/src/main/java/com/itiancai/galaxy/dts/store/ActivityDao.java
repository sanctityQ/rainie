package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.Status;

import java.util.List;

public interface ActivityDao {

  /**
   * 根据tx_id,status查询
   */
  Activity findByTxId(String txId);

  /**
   * 更新状态
   */
  int updateStatusByTxIdStatus(String txId, int status, int preStatus);

  /**
   * 修改完成标志
   */
  int finishActivity(String txId);

  /**
   * 查询未完成事务
   */
  List<String> listUnfinished(int index, int total, int recoveryInterval);

  /**
   * 开始处理tx
   */
  int lockTXByTxIdAndStatus(String txId, Status.Activity status, int maxRetryCount);

  /**
   * 回收tx; 重试次数+1,未收集状态
   */
  int reclaim(String txId);

  /**
   * 回收已收集tx,处理超时
   */
  int reclaimHandleTimeout(int handleTimeout);

  void save(Activity entity);

  /**
   * 增加处理次数
   * @param txId
   */
  int incrementRetryCountByTxId(String txId);
}
