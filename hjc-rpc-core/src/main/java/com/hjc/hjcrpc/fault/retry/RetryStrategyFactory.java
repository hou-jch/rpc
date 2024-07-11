package com.hjc.hjcrpc.fault.retry;

import com.hjc.hjcrpc.spi.SpiLoader;

/**
 * File Description: RetryStrategyFactory
 * Author: hou-jch
 * Date: 2024/7/11
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

  public static RetryStrategy getInstance(String key){
     return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
