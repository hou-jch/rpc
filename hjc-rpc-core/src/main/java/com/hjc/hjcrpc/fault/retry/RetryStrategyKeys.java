package com.hjc.hjcrpc.fault.retry;

/**
 * 重试策略-键名常量
 * File Description: RetryStrategyKeys
 * Author: hou-jch
 * Date: 2024/7/11
 */
public interface RetryStrategyKeys {

    /**
     *不重试
     */
    String NO_RETRY = "noRetry";
    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";
}
