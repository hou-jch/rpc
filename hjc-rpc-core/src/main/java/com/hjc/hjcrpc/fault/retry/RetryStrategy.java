package com.hjc.hjcrpc.fault.retry;

import com.hjc.hjcrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * File Description: RetryStrategy
 * Author: hou-jch
 * Date: 2024/7/11
 */
public interface RetryStrategy {
    RpcResponse doRetry(Callable<RpcResponse> callable) throws  Exception;
}
