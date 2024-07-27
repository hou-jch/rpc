package com.hjc.hjcrpc.springboot.starter.bootstrap;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.registry.LocalRegistry;
import com.hjc.hjcrpc.registry.Registry;
import com.hjc.hjcrpc.registry.RegistryFactory;
import com.hjc.hjcrpc.springboot.starter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 定义了一个 RpcProviderBootstrap 类，该类实现了 BeanPostProcessor 接口。BeanPostProcessor 接口允许开发者在 Spring 容器初始化 bean 的前后执行自定义逻辑。
 * File Description: RpcProviderBootstrap
 * Author: hou-jch
 * Date: 2024/7/15
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * Bean 初始化后执行，注册服务
     *这是 BeanPostProcessor 接口的方法之一，用于在 Spring 容器初始化 bean 之后执行自定义逻辑。这里主要用于注册 RPC 服务。
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取当前 bean 的类对象
        Class<?> beanClass = bean.getClass();

        // 检查当前 bean 是否带有 RpcService 注解，该注解通常用于标识一个类为 RPC 服务类。
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2. 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetalInfo serviceMetaInfo = new ServiceMetalInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
