package com.hjc.hjcrpc.bootstrap;

import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import com.hjc.hjcrpc.model.ServiceRegisterInfo;
import com.hjc.hjcrpc.registry.LocalRegistry;
import com.hjc.hjcrpc.registry.Registry;
import com.hjc.hjcrpc.registry.RegistryFactory;
import com.hjc.hjcrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * File Description: ProviderBootstrap
 * Author: hou-jch
 * Date: 2024/7/15
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList ){
        //RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());
            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetalInfo serviceMetalInfo = new ServiceMetalInfo();
            serviceMetalInfo.setServiceName(serviceName);
            serviceMetalInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetalInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetalInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName + "注册服务失败",e);
            }
        }
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());

    }
}
