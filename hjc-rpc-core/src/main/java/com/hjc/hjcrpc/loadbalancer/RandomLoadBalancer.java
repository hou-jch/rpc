package com.hjc.hjcrpc.loadbalancer;

import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 * File Description: RandomLoadBalancer
 * Author: hou-jch
 * Date: 2024/7/10
 */
public class RandomLoadBalancer implements LoadBalancer{

    private final Random random = new Random();

    @Override
    public ServiceMetalInfo select(Map<String, Object> requestParams, List<ServiceMetalInfo> serviceMetalInfoList) {
        int size = serviceMetalInfoList.size();
        if(size == 0){
            return null;
        }
        //只有一个服务，无需随机
        if(size == 1){
            return serviceMetalInfoList.get(0);
        }
        return serviceMetalInfoList.get(random.nextInt(size));
    }
}
