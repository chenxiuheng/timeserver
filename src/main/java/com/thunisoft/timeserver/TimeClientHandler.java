package com.thunisoft.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
 
import java.util.List;
import java.util.logging.Logger;
/**
 * Client 网络IO事件处理
 * @author xwalker
 *
 */
public class TimeClientHandler extends ByteToMessageDecoder {
    private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
    private  ByteBuf firstMessage;
    public TimeClientHandler(){
        byte[] req ="QUERY TIME ORDER".getBytes();
        firstMessage=Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("客户端收到服务器响应数据");
        ByteBuf buf= in;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("Now is:"+body);
        
        out.add(body);
    }
  
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        
        System.out.println("客户端active");
        ctx.writeAndFlush(firstMessage);
    }
     
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.warning("Unexpected exception from downstream:"+cause.getMessage());
        ctx.close();
        System.out.println("客户端异常退出");
    }
}
