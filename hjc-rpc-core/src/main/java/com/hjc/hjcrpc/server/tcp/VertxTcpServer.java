package com.hjc.hjcrpc.server.tcp;

import com.hjc.hjcrpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * File Description: VertxTcpServer
 * Author: hou-jch
 * Date: 2024/6/25
 */
@Slf4j
public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData) {
        return "hello,client".getBytes();
    }


    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }

    @Override
    public void doStart(int port) {
        //创建实例
        Vertx vertx = Vertx.vertx();
        //创建tcp服务器
        NetServer netServer = vertx.createNetServer();

        netServer.connectHandler(new TcpServerHandler());

        //启动tcp服务器监听端口
        netServer.listen(port,result ->{
            if (result.succeeded()) {
                System.out.println("TCP服务器启动成功，监听端口：" + port);
            }else{
                System.out.println("TCP服务器启动失败：" + result.cause());
            }
        });

    }
}
