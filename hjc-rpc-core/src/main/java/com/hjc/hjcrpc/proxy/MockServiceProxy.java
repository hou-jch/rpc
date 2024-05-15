package com.hjc.hjcrpc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock服务代理（JDK动态代理）
 * File Description: MockServiceProxy
 * Author: hou-jch
 * Date: 2024/5/15
 */
public class MockServiceProxy implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(MockServiceProxy.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据方法的返回值，生产特定的默认值对象
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}" , method.getName());
        return getDefaultObject(returnType);

    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type){
        if(type.isPrimitive()){
            if(type == boolean.class){
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }




        }
        //对象类型
        return null;
    }
}
