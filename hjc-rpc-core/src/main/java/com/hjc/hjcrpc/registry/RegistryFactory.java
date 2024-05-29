package com.hjc.hjcrpc.registry;

import com.hjc.hjcrpc.serializer.Serializer;
import com.hjc.hjcrpc.serializer.SerializerFactory;
import com.hjc.hjcrpc.spi.SpiLoader;

import java.util.Map;

/**
 * 注册中心工厂（获取注册中心对象）
 * File Description: RegistryFactory
 * Author: hou-jch
 * Date: 2024/5/28
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }
    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     */
    public static Registry  getInstance(String key){

        return SpiLoader.getInstance(Registry.class, key);
    }




}
