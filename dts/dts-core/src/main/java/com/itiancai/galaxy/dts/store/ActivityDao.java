package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.domain.Activity;

import java.util.List;

public interface ActivityDao {

  /**
   * 根据tx_id,status查询
   */
  Activity findByTxId(String txId);

  /**
   * 更新状态
   */
  int updateStatus(String txId, int status, int preStatus);

  /**
   * 修改完成标志
   */
  int finishActivity(String txId);

  /**
   * 查询未完成事务(成功|失败)
   */
  List<String> listSuccessOrFail(int index, int total, int maxRetryCount);

  /**
   * 查询未知超时事务 finish =未处理 & status=未知 & timeout & 重试次数<max
   */
  List<String> listUnknownAndTimeout(int index, int total, int maxRetryCount);

  /**
   * 开始处理tx
   */
  int handle(String txId, int maxRetryCount);

  /**
   * 回收tx; 重试次数+1,未收集状态
   */
  int reclaim(String txId);

  /**
   * 回收已收集tx,处理超时
   */
  int reclaimHandleTimeout(int handleTimeout);

  void save(Activity entity);
}
