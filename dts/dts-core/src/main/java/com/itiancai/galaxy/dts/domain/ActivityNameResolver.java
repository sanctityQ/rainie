package com.itiancai.galaxy.dts.domain;

import com.itiancai.galaxy.dts.DTSException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActivityNameResolver {

    private static Map<String,String> map = new HashMap<String, String>();
    /**
     * 检查name在服务端是否存在对应的bean
     * @param searchName 服务名 服务名.模块名:服务名
     */
    public void checkActivityName(String searchName){
        String[] names = getNames(searchName);
        if(!map.containsKey(names[0])){
            getRemotePath(searchName);
        }
    }

    /**
     * 获取地址
     * @param name
     * @return
     */
    public  String getPath(String name){
        String[] names = getNames(name);
        if(!map.containsKey(names[0])){
            getRemotePath(name);
        }
        return map.get(names[0]);
    }

    /**
     * TODO 在server中获取path并缓存
     * @param name
     */
    private  void getRemotePath(String name){
        //TODO 调用远程服务获取path
//        String path = "";
//        if (StringUtils.isEmpty(path)){
//            throw new DTSException(name +" search MethodName not in DtsService,DtsService not cache ");
//        }
//        map.put(name,"path");
    }


    /**
     * 检查字符串是否正确
     * @param name
     * @return
     */
    private  String[] getNames(String name){
        String[] names = name.split(":");
        if (names != null && names.length !=2){
            throw new DTSException("The activity name format is not correct,name is "+name);
        }
        return names;
    }

}
