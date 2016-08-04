package com.itiancai.galaxy.dts;

import com.itiancai.galaxy.dts.annotation.Action;
import com.itiancai.galaxy.dts.annotation.Activity;
import com.itiancai.galaxy.dts.annotation.Instruction;
import com.itiancai.galaxy.dts.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 事务入口
 */
@Component
public class  DTSController{

    @Autowired
    private DTSServiceManager manager;


    /**
     * 开启主事务,流程:
     * 1.生成id并存放在TreadLocal
     * 2.开启主事务(主事务状态为UNKNOWN)
     * @param bizId 业务id
     * @param type 服务名称
     * @param timeOut 超时时间
     */
    public void startActivity(String bizId, String type, int timeOut){
        manager.startActivity(bizId, type, timeOut);
    }

    /**
     * 主事务处理成功,子事务做SUCCESS或FIAL处理,流程如下:
     * 1.处理完主事务.(状态变更为SUCCESS FAIL)
     * 2.处理对应的子事务.(状态变更为PREPARE FAIL)
     * 3.isImmediately TRUE 实时处理子事务commit或rollback
     * @param isImmediately 是否实时处理
     * @param activityStatus 状态 SUCCESS FAIL
     *
     */
    public void finishActivity(boolean isImmediately, Status.Activity activityStatus){
        manager.finishActivity(activityStatus, isImmediately);
    }

    /**
     * 开启子事务
     * @param idempotency 幂等id
     * @param name 服务名称
     * @param context 请求参数json
     * @return long
     */
    public long startAction(String idempotency, String name, String context){
        return manager.startAction(idempotency, name, context);
    }


    /**
     * 子事务处理准备完成
     * @param status prepare
     * @param actionId
     *
     */
    public void finishAction(Status.Action status, long actionId){
        manager.finishAction(status, actionId);
    }

}
