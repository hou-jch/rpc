package com.hjc.hjcrpc.fault.tolerant;

import com.hjc.hjcrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 静默处理异常 - 容错策略
 * File Description: FailSafeTolerantStrategy
 * Author: hou-jch
 * Date: 2024/7/12
 */
@Slf4j
public class FailSafeTolerantStrategy  implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常",e);
        return new RpcResponse();
    }
}
