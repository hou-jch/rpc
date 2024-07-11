package com.hjc.hjcrpc.loadbalancer;

/**
 * 负载均衡器键名常量
 * File Description: LoadBalancerkeys
 * Author: hou-jch
 * Date: 2024/7/10
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */

    String ROUND_ROBIN = "roundRobin";
    String RANDOM = "random";
    String CONSISTENT_HASH="consistentHash";
}
