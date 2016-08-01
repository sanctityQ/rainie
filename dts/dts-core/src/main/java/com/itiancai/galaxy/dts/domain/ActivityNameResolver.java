package com.itiancai.galaxy.dts.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsp on 16/7/28.
 * 1.缓存name->path地址
 * 2.检查name是否存在对应的Bean
 */
@Component
public class ActivityNameResolver {

    //缓存name->path  NAME做一个解析
    private static Map<String,String> map = new HashMap<String, String>();
    /**
     * 检查name在服务端是否存在对应的bean
     * @param searchName 服务名 服务名.模块名:服务名
     */
    public void checkActivityName(String searchName){
        //TODO 1.解析name 获取服务名称
        String[] names = searchName.split(":");
        //TODO 2.判断name(服务名.模块名)是否被缓存,如缓存直接获取path,没有缓存直接调用server服务获取id
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
        //TODO 1.缓存中是否存在path,
        // TODO 如path不存在,在server中获取,如在没有直接抛出异常.
        String[] names = name.split(":");
        if(!map.containsKey(names[0])){
            getRemotePath(name);
        }
        return map.get(names[0]);
    }

    /**
     * 在server中获取path并缓存
     * @param name
     */
    private  void getRemotePath(String name){
        //TODO 在server中获取path 如不存直接抛出异常
        map.put(name,"path");
    }

}
