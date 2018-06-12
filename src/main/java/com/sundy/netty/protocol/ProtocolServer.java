package com.sundy.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月23日 16:17:46
 * 描述：自定义协议服务端，更多的demo==>https://github.com/waylau/netty-4-user-guide-demos
 */
public class ProtocolServer {

    /*一条完整协议的最大长度*/
    private static final int MAX_FRAME_LENGTH = 1024 * 1024 ;
    /*接收body的长度*/
    private static final int LENGTH_FIELD_LENGTH = 4;
    /*消息头部的偏移量，即是消息格式的(帧头+消息类型+保留字+序列号)的长度*/
    private static final int LENGTH_FIELD_OFFSET = 6;
    /*长度调整*/
    private static final int LENGTH_ADJUSTMENT = 0;
    /*最初的字节地带,即body字节的偏移量*/
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private int port;

    public ProtocolServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap strap = new ServerBootstrap();
            strap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
                    .option(ChannelOption.TCP_NODELAY, true)
                    //保持长连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder",new ProtocolDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
                            pipeline.addLast("encoder", new ProtocolEncoder());
                            pipeline.addLast("ping", new IdleStateHandler(120, 110, 100, TimeUnit.SECONDS));
                            pipeline.addLast(new ProtocolServerHandler());
                        }
                    });
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = strap.bind(port); // (7)

            future.addListener(new ChannelFutureListener() {

                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("Server bound port : "+port);
                    } else {
                        System.err.println("Bound attempt failed");
                        channelFuture.cause().printStackTrace();
                    }
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8082;
        new ProtocolServer(port).run();
    }
}
