package com.hjc.hjcrpc.springboot.starter.bootstrap;

import com.hjc.hjcrpc.proxy.ServiceProxyFactory;
import com.hjc.hjcrpc.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

import static com.hjc.hjcrpc.proxy.ServiceProxyFactory.getProxy;

/**
 * File Description: RpcConsumerBootstrap
 * Author: hou-jch
 * Date: 2024/7/15
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Bean 初始化后执行，注入服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取当前bean的类对象
        Class<?> beanClass = bean.getClass();

        // 获取当前bean的所有声明字段（包括私有的和公有的）
        Field[] declaredFields = beanClass.getDeclaredFields();

        // 遍历所有字段
        for (Field field : declaredFields) {
            // 检查字段上是否有RpcReference注解，这个注解通常用于标记需要代理的RPC接口或类
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 获取注解中指定的接口类，如果没有指定则使用字段的类型作为接口类
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }

                // 将字段设置为可访问（主要是为了可以设置私有字段）
                field.setAccessible(true);

                // 根据接口类生成对应的代理对象（代理对象主要用于RPC调用）
                Object proxyObject = getProxy(interfaceClass);

                try {
                    // 将生成的代理对象设置到bean的对应字段上，实现RPC功能
                    field.set(bean, proxyObject);
                    // 设置完成后将字段重新设置为不可访问（保护字段的访问权限）
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    // 如果在尝试设置字段值时发生异常，则抛出运行时异常并带上原始异常信息
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        // 调用父类的postProcessAfterInitialization方法完成Bean的初始化后处理，这是BeanPostProcessor的标准流程之一。如果不调用此方法，可能会影响其他BeanPostProcessor或Spring容器的工作。
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}