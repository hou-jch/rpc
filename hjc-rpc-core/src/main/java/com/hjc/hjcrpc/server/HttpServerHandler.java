package com.hjc.hjcrpc.server;

/**
 * File Description: HttpServerHandler
 * Author: hou-jch
 * Date: 2024/5/14
 */

import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.model.RpcRequest;
import com.hjc.hjcrpc.model.RpcResponse;
import com.hjc.hjcrpc.registry.LocalRegistry;
import com.hjc.hjcrpc.serializer.JdkSerializer;
import com.hjc.hjcrpc.serializer.Serializer;
import com.hjc.hjcrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.Http1xServerRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理,先拿到请求，处理后响应
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //输出
        System.out.println("received request: " + httpServerRequest.method() + " " + httpServerRequest.uri());
        //异步处理HTTP请求
        httpServerRequest.bodyHandler(body->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes,RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest == null){
                rpcResponse.setMessage("request is null");
                doResponse(httpServerRequest,rpcResponse,serializer);
                return;
            }

            try {
                //获取要调用的服务实现类，通过反射调

                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 通过反射获取指定服务的方法
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                // 调用指定方法，并传入参数
                Object object = method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                // 将方法返回值设置到响应中
                rpcResponse.setData(object);
                rpcResponse.setDataType(method.getReturnType());
                // 设置响应消息
                rpcResponse.setMessage("ok");




            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(httpServerRequest,rpcResponse,serializer);
        });
        //





    }


    /**
     * 响应
     * @param httpServerRequest
     * @param serializer
     */

    void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse,Serializer serializer){
        HttpServerResponse response = httpServerRequest.response().putHeader("content-type","applicaton/json");
        try {
            //序列化
            byte[] serialize = serializer.serialize(rpcResponse);
            //在 Vert.x 中，Buffer 类代表一个可增长的字节缓冲区，通过它可以方便地进行字节数据的读写操作。
            // 这里的 Buffer.buffer(serialize) 作用是将字节数组 serialize 包装成一个 Vert.x 的 Buffer 对象，以便后续进行网络传输。
            response.end(Buffer.buffer(serialize));
        }catch (IOException e){
            //printStackTrace() 是 Throwable 类中的一个方法，用于打印异常的堆栈跟踪信息。
            e.printStackTrace();
            response.end(Buffer.buffer());

        }

        }



}
