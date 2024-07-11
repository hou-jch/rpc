package com.hjc.hjcrpc.loadbalancer;

import com.hjc.hjcrpc.spi.SpiLoader;

/**
 * File Description: LoadBalancerFactory
 * Author: hou-jch
 * Date: 2024/7/10
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFALUT_LOAD_LOADBALANCER = new RoundRobinLoadBalancer();


    /**
     * 获取实例
     */
    public static LoadBalancer getLoadBalancer(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}