package com.hjc.hjcrpc.springboot.starter.bootstrap;

import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.server.tcp.VertxTcpServer;
import com.hjc.hjcrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架全局启动类
 * File Description: RpcInitBootstrap
 * Author: hou-jch
 * Date: 2024/7/15
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            // 在Spring初始化时执行，注册Bean并初始化RPC框架。
            // 注册具体的Bean定义到Spring的BeanDefinitionRegistry中。
            // 这两个参数分别是导入类的注解元数据（用于获取注解属性）和Bean定义注册类（用于注册Bean）。

            // 获取EnableRpc注解的属性值中的needServer字段，该字段表示是否需要启动RPC服务器。
            boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                    .get("needServer");

            // 初始化RPC应用框架（包括配置和注册中心等）。这个是RPC框架的入口点。
            RpcApplication.init();

            // 获取全局的RPC配置。
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            // 根据needServer的值决定是否启动RPC服务器。如果needServer为true，则创建一个新的VertxTcpServer实例并启动它。
            if (needServer) {
                VertxTcpServer vertxTcpServer = new VertxTcpServer();  // 创建TCP服务器实例
                vertxTcpServer.doStart(rpcConfig.getServerPort());     // 启动TCP服务器，使用配置中的端口号
            } else {  // 如果不需要启动服务器，则打印一条日志信息
                log.info("不启动 server");
            }
        }
    }