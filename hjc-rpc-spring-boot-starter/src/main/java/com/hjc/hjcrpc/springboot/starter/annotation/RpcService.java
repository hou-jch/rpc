package com.hjc.hjcrpc.springboot.starter.annotation;
import com.hjc.hjcrpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 服务提供者注解（用于注册服务）
 * File Description: RpcService
 * Author: hou-jch
 * Date: 2024/7/15
 */


// @Target 注解用来指定我们自定义注解的应用目标，这里是类（TYPE），可以标记到类、接口、枚举或注解类型上
@Target({ElementType.TYPE})
// @Retention 注解用来指定我们的注解在什么情况下可用，这里使用的是 RUNTIME 级别，表示注解不仅被保存到class文件中，并且在运行时可以通过反射机制读取到
@Retention(RetentionPolicy.RUNTIME)
// @Component 注解是Spring注解，标识该注解修饰的类会被Spring IOC容器管理，说明这个类被建成Spring Bean
@Component
// 声明自定义注解 @RpcService
public @interface RpcService {

    // 定义一个名为 'interfaceClass' 的元素，用于指定服务接口的类，默认值为 void.class。
    // 这个元素可以用于告诉框架这个服务实现了哪个接口
    // 对应的元素将会在编译时被分析和检查
    Class<?> interfaceClass() default void.class;

    // 定义一个名为 'serviceVersion' 的元素，用于指定服务的版本，默认为 RpcConstant.DEFAULT_SERVICE_VERSION。
    // 这个元素可以用于版本控制，区分同一个接口不同版本的实现
    // 对应的版本元素将会在编译时被分析和检查
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
