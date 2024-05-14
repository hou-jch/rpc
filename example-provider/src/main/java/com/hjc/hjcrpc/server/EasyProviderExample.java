package com.hjc.hjcrpc.server;

import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.server.registry.LocalRegistry;
import lombok.val;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
