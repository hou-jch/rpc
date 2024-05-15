package com.hjc.hjcrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResource;
import cn.hutool.http.HttpResponse;
import com.hjc.hjcrpc.model.RpcRequest;
import com.hjc.hjcrpc.model.RpcResponse;
import com.hjc.hjcrpc.serializer.JdkSerializer;
import com.hjc.hjcrpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK动态代理）
 * File Description: ServiceProxy
 * Author: hou-jch
 * Date: 2024/5/14
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        //序列化
        byte[] serialize = serializer.serialize(rpcRequest);
        //发送请求
        // 放在 try 的圆括号内，目的是利用 Java 7 引入的 try-with-resources 语法。
        // 这一语法用于自动管理那些实现了 AutoCloseable 接口的资源，例如输入输出流、数据库连接和 HTTP 客户端响应对象。
        //自动资源管理:
                    //try-with-resources 确保在 try 块执行完之后，自动调用资源的 close() 方法。这样可以避免手动关闭资源时的繁琐代码，并减少忘记关闭资源导致的资源泄露风险。
        //简化代码:
                 //对于资源管理，这种语法提升了代码的简洁性和可读性。不需要显式地在 finally 块中关闭资源，编译器会生成相应的代码来确保资源关闭。
        //处理自动关闭异常:
                        //如果 close() 方法在关闭资源时抛出异常，try-with-resources 块会处理这种情况，并确保任何正常的抛出也会被抛出和捕获。
        try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(serialize)
                    .execute()) {
            RpcResponse rpcResponse = serializer.deserialize(httpResponse.bodyBytes(), RpcResponse.class);
            return rpcResponse.getData();

        }catch (IOException e){
            e.printStackTrace();
        }



        return null;
    }
}
