package com.hjc.hjcrpc.serializer;

import com.hjc.hjcrpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.lang.Singleton.put;

/**
 * 序列化器工厂（用于获取序列化器对象）
 * File Description: SerializerFactory
 * Author: hou-jch
 * Date: 2024/5/16
 */
public class SerializerFactory {

    private static Map <String,Class<?>> instance;
//    /**
//     * 序列化映射(用于实现单例）
//     */
//    private static final Map<String,Serializer> KEY_SERIALIZER_MAP = new HashMap<String,Serializer>();
//    {{
//        put(SerializerKeys.JDK,new JdkSerializer());
//        put(SerializerKeys.JSON,new JsonSerializer());
//        put(SerializerKeys.KRYO,new KryoSerializer());
//        put(SerializerKeys.HESSIAN,new HessianSerializer());
//    }}

    private SerializerFactory(){}
    public static void getInstance(){

    }


    /**
     * 默认序列化器
     */
//    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        if(instance == null){
            synchronized (SerializerFactory.class){
                if (instance == null){
                    instance = SpiLoader.load(Serializer.class);
                }
            }
        }
        System.out.println(key);
        return SpiLoader.getInstance(Serializer.class,key);
    }
}
