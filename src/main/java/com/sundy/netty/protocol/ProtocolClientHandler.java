package com.sundy.netty.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 15:40:01
 * 描述：
 */
public class ProtocolClientHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        System.out.println(System.currentTimeMillis());
        Channel incoming = ctx.channel();
        System.out.println("Server->Client:"+incoming.remoteAddress()+obj.toString());
        if(obj instanceof ProtocolMessage) {
            ProtocolMessage msg = (ProtocolMessage)obj;
            System.out.println("Server->Client:"+incoming.remoteAddress()+msg.getBody());
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.flush();
        System.out.println("channelReadComplete channelReadComplete channelReadComplete");
    }

    /**
     * 一段时间未进行读写操作 回调
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state().equals(IdleState.READER_IDLE)) {
                //未进行读操作
                System.out.println("READER_IDLE");
                // 超时关闭channel
                ctx.close();

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {

                System.out.println("write idle");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                //未进行读写
                System.out.println("ALL_IDLE");
                // 发送心跳消息
                ctx.channel().writeAndFlush(new ProtocolMessage());
            }
        }
    }

}
