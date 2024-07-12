package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.spi.SpiLoader;

/**
 * File Description: TolerantStrategyFactory
 * Author: hou-jch
 * Date: 2024/7/12
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }
/**
 * 默认容错策略
 */
    private static final TolerantStrategy DEFAULT_RETRY_STRATEGY = new FailFastTolerantStrategy();


    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
