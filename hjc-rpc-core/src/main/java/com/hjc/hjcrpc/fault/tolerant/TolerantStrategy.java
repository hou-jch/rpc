package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 * File Description: TolerantStrategy
 * Author: hou-jch
 * Date: 2024/7/12
 */
public interface TolerantStrategy {
    /**
     * 容错
     * @param context
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
