package com.hjc.hjcrpc.bootstrap;

import com.hjc.hjcrpc.RpcApplication;

/**
 * File Description: ConsumerBootstrap
 * Author: hou-jch
 * Date: 2024/7/15
 */
public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init() {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
