package com.hjc.example.common.model.example.consumer;

import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.utils.ConfigUtils;

/**
 * 简单消费者示例
 * File Description: ConsumerExample
 * Author: hou-jch
 * Date: 2024/5/15
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

    }
}
