package com.hjc.hjcrpc.server;


import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建Vertx示例
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server  = vertx.createHttpServer();
        server.requestHandler(new HttpServerHandler());

//        server.requestHandler(request -> {
//            //处理HTTP请求
//            System.out.println("Received result:" + request.method() + " " + request.uri());
//            request.response().putHeader("content-type","text/plain").end("Hello, idiot");
//        });
        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("Server is now listening on port " + port);
            }else{
                System.out.println("Failed to start server :" + result.cause());
            }
        });
    }
}
