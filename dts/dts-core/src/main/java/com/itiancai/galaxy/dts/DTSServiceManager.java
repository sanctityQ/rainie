package com.itiancai.galaxy.dts;

import com.itiancai.galaxy.dts.dao.ActionDao;
import com.itiancai.galaxy.dts.dao.ActivityDao;
import com.itiancai.galaxy.dts.domain.*;
import com.itiancai.galaxy.dts.domain.Action;
import com.twitter.finagle.http.Response;
import com.twitter.util.Function;
import com.twitter.util.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import scala.Function1;
import scala.runtime.BoxedUnit;

import java.util.Date;
import java.util.List;

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
     * @param bizId 业务id
     * @param businessType 服务名称
     * @param timeOut 超时时间
     * @throws Exception
     */
    @Transactional
    public void startActivity(String bizId, String businessType, int timeOut){
        activityNameResolver.checkActivityName(businessType);
        long txId = Long.parseLong(ContextsLocal.current_txId());
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
        long txId = Long.parseLong(ContextsLocal.current_txId());
        List<Action> actionList = actionDao.findActionByTxId(txId);
        updateActivityAction(status, txId,actionList);
        if(isImmediately){
            executeAction(actionList);
        }
    }

    /**
     * 远程调用子事务commit rollback方法
     * @param list
     */
    @Transactional
    public void executeAction(List<Action> list){
        //TODO 实时处理
        boolean isSuccess = true;
        if(!CollectionUtils.isEmpty(list)){
            for(Action action : list){
                String path = actionNameResolver.getPath(action.getServiceName());
                String name = actionNameResolver.getNames(action.getServiceName())[1];
                String instructionId = action.getInstructionId();
                //TODO 调用客户端
                //TODO 子事务处理失败 直接break,isSuccess = false
                Future<Response> response = HttpClient.handleRPC("",null);
                if(response.isDefined()){

                }
            }
            if(isSuccess){
                Activity activity = activityDao.findActivityByTxId(list.get(0).getTxId());
                activity.setFinish(1);
                activityDao.save(activity);
            }
        }
    }

    /**
     * 修改activity action数据
     * @param status
     * @param txId
     */
    @Transactional
    private void updateActivityAction(Status.Activity status,long txId,List<Action> actionList){
        Activity activity = activityDao.findActivityByTxId(txId);
        if(activity != null){
            activity.setStatus(status);
            activityDao.save(activity);
            if(!CollectionUtils.isEmpty(actionList)){
                for(Action action : actionList){
                    action.setStatus(Status.Action.getStatusAction(status.getStatusActivity()));
                }
                actionDao.save(actionList);
            }
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
    @Transactional
    public long startAction( String instructionId, String serviceName, String context){
        actionNameResolver.checkActionName(serviceName);
        Date date = new Date();
        long txId = Long.parseLong(ContextsLocal.current_txId());
        Long actionId = IdGenerator.genActionId();
        Action action = new Action(txId,actionId,
            Status.Action.UNKNOWN, serviceName, date,
                date, context, instructionId);
        actionDao.save(action);
        return action.getActionId();
    }

    /**
     * 子事务处理状态变更处理
     * @param status 事务变更状态 PREPARE FAIL
     * @throws Exception
     */
    @Transactional
    public void finishAction(Status.Action status, long actionId){
        Action action = actionDao.findActionByActionId(actionId);
        action.setStatus(status);
        actionDao.save(action);
    }


}
