package com.hjc.example.provider;

import com.hjc.example.common.service.ColorService;
import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import com.hjc.hjcrpc.registry.LocalRegistry;
import com.hjc.hjcrpc.registry.Registry;
import com.hjc.hjcrpc.registry.RegistryFactory;
import com.hjc.hjcrpc.server.ColorServiceImpl;
import com.hjc.hjcrpc.server.HttpServer;
import com.hjc.hjcrpc.server.UserServiceImpl;
import com.hjc.hjcrpc.server.VertxHttpServer;

import java.util.concurrent.ExecutionException;

/**
 * File Description: ProviderExample
 * Author: hou-jch
 * Date: 2024/5/28
 */
public class ProviderExample {

    public static void main(String[] args) {
        //框架初始化
        RpcApplication.init();

        //注册服务

        String serviceName = UserService.class.getName();

        String serviceNames = ColorService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);
        LocalRegistry.register(serviceNames, ColorServiceImpl.class);
        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetalInfo serviceMetalInfo = new ServiceMetalInfo();
        serviceMetalInfo.setServiceName(serviceName);
        serviceMetalInfo.setServiceHost("localhost");
        serviceMetalInfo.setServicePort(8080);
        serviceMetalInfo.setServiceAddress(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());

        ServiceMetalInfo serviceMetalInfo1 = new ServiceMetalInfo();
        serviceMetalInfo1.setServiceName(serviceNames);
        serviceMetalInfo1.setServiceHost("localhost");
        serviceMetalInfo1.setServicePort(8080);
        serviceMetalInfo1.setServiceAddress(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());
        try {

            registry.register(serviceMetalInfo);
            registry.register(serviceMetalInfo1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //启动web服务

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
