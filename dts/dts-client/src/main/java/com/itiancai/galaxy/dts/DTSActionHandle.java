package com.itiancai.galaxy.dts;

/**
 * 分布式事务处理接口 1.定义commit及rollback接
 * 所有使用DTS注解声名的方法都必须继承此接口
 */
public interface DTSActionHandle {

    /**
     * 子事务提交方法
     * @param instructionId 幂等id
     */
    void commit(String instructionId);

    /**
     * 子事务回滚方法
     * @param instructionId 幂等id
     */
    void rollback(String instructionId);

    /**
     * 获取服务名
     * @return
     */
    String name();
}
