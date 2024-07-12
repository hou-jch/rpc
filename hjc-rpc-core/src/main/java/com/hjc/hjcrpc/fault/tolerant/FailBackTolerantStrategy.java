package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 * File Description: FailBackTolerantStrategy
 * Author: hou-jch
 * Date: 2024/7/12
 */
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {

        //待扩展
        return null;
    }
}
