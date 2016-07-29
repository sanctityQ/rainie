package com.itiancai.galaxy.dts;

import com.itiancai.galaxy.dts.dao.ActionDao;
import com.itiancai.galaxy.dts.dao.ActivityDao;
import com.itiancai.galaxy.dts.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by lsp on 16/7/28.
 */
@Service
class DTSServiceManager {

    @Autowired
    private ActionDao actionDao;
    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private ActionNameResolver actionNameResolver;
    @Autowired
    private ActivityNameResolver activityNameResolver;

    /**
     * 开启主事务,流程如下
     * 1.判断name对应的bean是否存在,且缓存
     * 2.生成tx_id,tx_id并存放在域中
     * 3.组装数据并保存
     *
     * @param bizId 业务id
     * @param businessType 服务名称
     * @throws Exception
     */
    public void startActivity(String bizId, String businessType, long timeOut){
        activityNameResolver.checkActivityName(businessType);
        long txId = IdGenerator.genTXId();
        Date date = new Date();
        Activity activity = new Activity(txId,Status.Activity.UNKNOWN
                ,businessType,date,timeOut,
                 date,0,bizId);
        activityDao.save(activity);
    }


    /**
     * 主事务处理成功,子事务做commit 流程:
     * 1.tx_id获取当前主事务数据,修改数据状态
     * 2.变更子事务状态
     * 3.isImmediately true action回调对应得commit/rollback
     * @param status 方法名称 SUCCESS FAIL
     * @param isImmediately 是否实时处理
     * @throws Exception
     */
    public void finishActivity(Status.Activity status, boolean isImmediately) {
        long txId = 0L;
        Activity activity = activityDao.findActivityByTxIdStatus(txId);
        activity.setStatus(status);
        activityDao.save(activity);
        List<Action> actionList = actionDao.listActionByTxId(txId);
        if(!CollectionUtils.isEmpty(actionList)){
            for(Action action : actionList){
                action.setStatus(Status.Action.getStatusAction(status.getStatus()));
            }
            actionDao.save(actionList);
        }
        if(isImmediately){
            //TODO 实时处理
        }
    }

    /**
     * 开启子事务,流程如下:
     * 1.判断name对应的bean及tx_id是否存在,且缓存
     * 2.组装Action 保存数据
     * @param instructionId 幂等id
     * @param serviceName 服务名称
     * @param context 请求参数json
     */
    public void startAction( String instructionId, String serviceName, String context){
        actionNameResolver.checkActionName(serviceName);
        Date date = new Date();
        Action action = new Action(0L,IdGenerator.genActionId(),
            Status.Action.UNKNOWN, serviceName, date,
                date, context, instructionId);
        actionDao.save(action);
    }

    /**
     * 子事务处理状态变更处理
     * @param actionId 主键id
     * @param status 事务变更状态 PREPARE FAIL
     * @throws Exception
     */
    public void finishAction(long actionId,Status.Action status){
        Action action = actionDao.listActionByActionId(actionId);
        action.setStatus(status);
        actionDao.save(action);
    }
}
