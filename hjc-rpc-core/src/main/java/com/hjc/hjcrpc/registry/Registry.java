package com.hjc.hjcrpc.registry;

import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心接口
 * File Description: Registry
 * Author: hou-jch
 * Date: 2024/5/26
 */
public interface Registry   {

    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     */
    void register(ServiceMetalInfo serviceMetalInfo) throws ExecutionException, InterruptedException;

    /**
     * 注销服务（服务端）
     */
    void unRegister(ServiceMetalInfo serviceMetalInfo);

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     */
    List<ServiceMetalInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */

    void destroy();

    /**
     * 心跳检测
     */

    void heartBeat();

    /**
     * 监听
     */
    void watch(String serviceNodeKey);

}
