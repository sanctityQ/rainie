package com.itiancai.galaxy.dts;

/**
 * Created by lsp on 16/8/1.
 *
 * 主事务查询接口
 */
public interface DTSActivityHandle {

    /**
     * 根据业务id查询接口事务处理完成
     * 业务处理完成返回-SUCCESS ,业务处理未完成返回-FAIL
     *
     * @param bizId 业务id
     * @return
     */
    String search(String bizId);

    /**
     * 获取服务名称
     *
     * @return
     */
    String name();
}
