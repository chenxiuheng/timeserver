package com.thunisoft.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;
import java.util.logging.Logger;

/**
 * Client 网络IO事件处理
 * @author xwalker
 */
public class TimeHandler extends ByteToMessageDecoder {
    private static final Logger logger = Logger.getLogger(TimeHandler.class.getName());
    private int seqNo = 0;

    private Frame frame = new Frame();

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

        ctx.writeAndFlush(frame.encode());
        frame.seqNo ++;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            ctx.writeAndFlush(frame.encode());
            frame.seqNo ++;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception from downstream:" + cause.getMessage());
        ctx.close();
        System.out.println("客户端异常退出");
    }
}
