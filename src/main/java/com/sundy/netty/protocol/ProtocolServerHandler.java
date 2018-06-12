package com.sundy.netty.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 14:21:36
 * 描述：协议端接收的的数据
 */
public class ProtocolServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel incoming = ctx.channel();
        if(msg instanceof ProtocolMessage) {
            ProtocolMessage message = (ProtocolMessage)msg;
            System.out.println("Client->Server:"+incoming.remoteAddress()+"\n"+message.getBody());
            message.setBody("傻屌，服务端收到信息了！");
            incoming.write(message);//发送给客户端的信息
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        throw new Exception("protocol server handler exception ,the message is "+cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {

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
                //MsgHandleService.getInstance().sendMsgUtil.sendHeartMessage(ctx);
//                ctx.channel().writeAndFlush(buildMsg());
            }

        }
    }

    /**
     * channel失效
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        System.out.println("disconnected client ip is "+clientIp);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();

        System.out.println("accept a active connection!"+ctx.channel().remoteAddress().toString());
        System.out.println("client"+ctx.channel().remoteAddress().toString()+" connect success."+" clientIp is "+clientIp);
    }
}
