package com.thunisoft.timeserver;

import java.util.List;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Client 网络IO事件处理
 * @author xwalker
 */
public class TimeHandler extends ByteToMessageDecoder {
    private static final Logger logger = Logger.getLogger(TimeHandler.class.getName());

    private int seqNo;

    public TimeHandler() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Frame frame = Frame.decode(in);
        if (null != frame) {
            System.out.println(frame);
            out.add(frame);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("shoudao yige qingqiu" + ctx.channel().remoteAddress());
        
        Frame frame = new Frame(seqNo, 1400);
        ctx.writeAndFlush(frame.encode());
        seqNo ++;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idle = (IdleStateEvent)evt;
            if (idle.state() == IdleState.WRITER_IDLE) {
            	 Frame frame = new Frame(seqNo, 1400);
                 ctx.writeAndFlush(frame.encode());
                 seqNo ++;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception from downstream:" + cause.getMessage());
        ctx.close();
        System.out.println("客户端异常退出");
    }
}
