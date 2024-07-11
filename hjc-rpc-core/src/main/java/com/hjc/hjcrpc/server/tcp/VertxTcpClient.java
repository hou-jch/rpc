package com.hjc.hjcrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.hjc.hjcrpc.RpcApplication;
import com.hjc.hjcrpc.model.RpcRequest;
import com.hjc.hjcrpc.model.RpcResponse;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import com.hjc.hjcrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * File Description: VertxTcpClient
 * Author: hou-jch
 * Date: 2024/6/25
 */
public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetalInfo serviceMetalInfo) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
//CompletableFuture的设计是为了在非阻塞、异步操作中使用。直接抛出异常会导致异步代码块中断，但不会通知CompletableFuture，这使得它无法处理异常情景。因此，使用completeExceptionally是在异步任务中正确通知错误的唯一方法。
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetalInfo.getServicePort(), serviceMetalInfo.getServiceHost(), result -> {

            if (result.succeeded()) {
                System.out.println("Connected to tcp server ");
                NetSocket netSocket = result.result();
                //发送数据
                //构造消息
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);
                //编码请求
                try {
                    Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
                    netSocket.write(buffer);
                } catch (IOException e) {
                    responseFuture.completeExceptionally(new RuntimeException("协议消息解码错误"));
                }
                TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(rpcResponseProtocolMessage.getBody());
                    } catch (IOException e) {
                        responseFuture.completeExceptionally(new RuntimeException("协议消息解码错误"));
                    }
                });
                netSocket.handler(tcpBufferHandlerWrapper);

            } else {
                //这行代码可以触发重试策略
                //用于显式地完成一个CompletableFuture并抛出指定的异常。当调用这个方法时，它会通知所有等待这个CompletableFuture的线程和回调函数有一个异常发生。
                //throw new RuntimeException()：这种方式只是抛出一个异常，除非异常被捕获并传递到CompletableFuture内部。因此，直接抛出异常并不会自动传播到CompletableFuture的消费者。
                responseFuture.completeExceptionally(new RuntimeException("Failed to connect to TCP server"));

            }
        });
        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse;
    }

















    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost",result->{
            if(result.succeeded()){
                System.out.println("连接成功");
                io.vertx.core.net.NetSocket socket = result.result();
                //发送数据
                socket.write("hello,Server");
                //接受响应
                socket.handler(buffer -> {
                    System.out.println("接收到响应："+buffer.toString());
                });
            }else{
                System.err.println("连接失败");
            }
        });
    }

    public static void main(String[] args) {

        VertxTcpClient vertxTcpClient = new VertxTcpClient();
        vertxTcpClient.start();
    }
}
