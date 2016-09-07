package com.itiancai.galaxy.dts;

/**
 *
 * 用来提交协作者事务或者回滚
 */
public interface XAResourceActionService {

    /**
     * 提交
     *
     * @param instructionId 幂等id
     */
    boolean commit(String instructionId);

    /**
     * 回滚
     *
     * @param instructionId 幂等id
     */
    boolean rollback(String instructionId);

    /**
     * 获取服务名
     *
     * @return
     */
    String name();
}
