package com.hjc.hjcrpc.config;

import lombok.Data;

/**
 * RPC框架注册中下心配置
 * File Description: RegistryConfig
 * Author: hou-jch
 * Date: 2024/5/26
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址
     */
    private String address = "http://192.168.10.107:2379";
    /**
     * 注册中心用户名
     */
    private String username ;
    /**
     * 注册中心密码
     */
    private String password ;
    /**
     * 超时时间（毫秒）
     */
    private Long timeout = 10000L;

}
