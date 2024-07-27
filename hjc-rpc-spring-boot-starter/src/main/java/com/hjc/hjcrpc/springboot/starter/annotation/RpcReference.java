package com.hjc.hjcrpc.springboot.starter.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.hjc.hjcrpc.constant.RpcConstant;
import com.hjc.hjcrpc.fault.tolerant.TolerantStrategyKeys;
import com.hjc.hjcrpc.loadbalancer.LoadBalancerKeys;
import com.hjc.hjcrpc.fault.retry.RetryStrategyKeys;
/**
 * 服务消费者注解（用于注入服务）
 * File Description: RpcReference
 * Author: hou-jch
 * Date: 2024/7/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    String retryStrategy() default RetryStrategyKeys.NO_RETRY;

    /**
     * 容错策略
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     */
    boolean mock() default false;

}
