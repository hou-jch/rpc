package com.hjc.hjcrpc.loadbalancer;

import com.hjc.hjcrpc.model.ServiceMetalInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash负载均衡器
 * File Description: ConsistentHashLoadBaiancer
 * Author: hou-jch
 * Date: 2024/7/10
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性hash环，存放虚拟节点
     */
    private final TreeMap<Integer,ServiceMetalInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetalInfo select(Map<String, Object> requestParams, List<ServiceMetalInfo> serviceMetalInfoList) {
        if (serviceMetalInfoList.isEmpty()){
            return null;
        }
        //构建虚拟节点环
        for (ServiceMetalInfo serviceMetalInfo : serviceMetalInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetalInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash,serviceMetalInfo);
            }
        }

        //获取调用请求的hash值
        int hash = getHash(requestParams);
        Map.Entry<Integer, ServiceMetalInfo> entity = virtualNodes.ceilingEntry(hash);
        if(entity == null){
            //如果没有大于等于请求hash值的虚拟节点，则返回首部的节点
            entity = virtualNodes.firstEntry();
        }
        return entity.getValue();
    }

    private int getHash(Object key){
        return key.hashCode();
    }
}
