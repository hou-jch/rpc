package com.hjc.hjcrpc.loadbalancer;

import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 * File Description: RoundRobinLoadBalancer
 * Author: hou-jch
 * Date: 2024/7/10
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public ServiceMetalInfo select(Map<String, Object> requestParams, List<ServiceMetalInfo> serviceMetalInfoList) {
        if(serviceMetalInfoList.isEmpty()){
            return null;
        }
        int size = serviceMetalInfoList.size();
        //只有一个服务，无需轮询
        if(size == 1){
            return serviceMetalInfoList.get(0);
        }

        int index = currentIndex.getAndIncrement() % size;
        return serviceMetalInfoList.get(index);
    }
}
