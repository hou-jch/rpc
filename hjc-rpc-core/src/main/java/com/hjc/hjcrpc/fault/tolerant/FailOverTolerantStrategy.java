package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.model.RpcResponse;

import java.util.Map;

/**
 * 转移到其他服务节点 - 容错策略
 * File Description: FailOverTolerantStrategy
 * Author: hou-jch
 * Date: 2024/7/12
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //等待扩展
        return null;
    }
}
