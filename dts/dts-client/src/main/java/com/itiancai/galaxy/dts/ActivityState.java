package com.itiancai.galaxy.dts;

/**
 * 主事务查询接口
 */
public interface ActivityState {

    /**
     * 根据业务id查询接口事务处理完成
     * @param businessId 业务id
     * @return 0-成功 1-失败 2-未知
     */
    int isDone(String businessId);

    /**
     * 获取服务名称
     *
     * @return
     */
    String name();
}
