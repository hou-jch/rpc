package com.hjc.hjcrpc.registry;

import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * 注册中心本地缓存
 * File Description: RegistryServiceCache
 * Author: hou-jch
 * Date: 2024/6/1
 */
public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    Map<String,List<ServiceMetalInfo>> serviceCache = new HashMap<>();

    /**
     * 写缓存
     */
    void writeCache(String serviceKey , List<ServiceMetalInfo> newServiceCache){

        this.serviceCache.put(serviceKey,newServiceCache) ;
    }

    /**
     * 读缓存
     */
    List<ServiceMetalInfo> readCache(String serviceKey){
        return this.serviceCache.get(serviceKey);
    }

    /**
     * 清空缓存
     */
    void clearCache(String serviceNodeKey){
        this.serviceCache.remove(serviceNodeKey);
    }
}
