package com.hjc.example;

import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.registry.LocalRegistry;
import com.hjc.hjcrpc.server.HttpServer;
import com.hjc.hjcrpc.server.UserServiceImpl;
import com.hjc.hjcrpc.server.VertxHttpServer;

/**
 * 简单服务提供者示例
 * File Description: EasyProviderExample
 * Author: hou-jch
 * Date: 2024/5/15
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //框架初始化
        RpcApplication.init();
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

    }
}
