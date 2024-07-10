package com.hjc.hjcrpc.server.tcp;

import com.hjc.hjcrpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.parsetools.RecordParser;

import io.vertx.core.buffer.Buffer;

/**
 * 装饰着模式 使用recordParser对原有的buffer处理能力进行增强
 * File Description: TcpBufferHandlerWrapper
 * Author: hou-jch
 * Date: 2024/7/10
 */

public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParser(bufferHandler);
    }
    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler){
        //构造 parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            //初始化
            int size = -1;
            //一次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                //首先会检查size变量，如果为-1，说明当前读取的是消息头，会从字节流中读取消息体的长度并设置为size，然后将收到的数据追加到resultBuffer中。
                if(-1 == size){
                    //读取消息体长度
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);

                }else{
                    //如果size不为-1，则说明当前在读取消息体，会将数据追加到resultBuffer中，并在达到消息体长度后处理请求，并重置一些变量以准备接收下一条消息。
                    resultBuffer.appendBuffer(buffer);
                    System.out.println(recordParser.toString());
                    bufferHandler.handle(resultBuffer);
                    //重置一轮
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
}
