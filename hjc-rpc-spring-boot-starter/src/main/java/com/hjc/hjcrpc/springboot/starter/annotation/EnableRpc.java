package com.hjc.hjcrpc.springboot.starter.annotation;

/**
 * File Description: EnableRpc
 * Author: hou-jch
 * Date: 2024/7/15
 */
import com.hjc.hjcrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.hjc.hjcrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.hjc.hjcrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Rpc 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default true;
}
