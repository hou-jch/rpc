package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败-容错策略（立刻通知外层调用方）
 * File Description: FailFastTolerantStrategy
 * Author: hou-jch
 * Date: 2024/7/12
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错",e);
    }
}
