package com.hjc.hjcrpc.loadbalancer;

import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.List;
import java.util.Map;

/**负载均衡器，消费端使用
 * File Description: LoadBalancer
 * Author: hou-jch
 * Date: 2024/7/10
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     * @param requestParams
     * @param serviceMetalInfoList
     * @return
     */
    ServiceMetalInfo select(Map<String,Object> requestParams, List<ServiceMetalInfo> serviceMetalInfoList);
}
