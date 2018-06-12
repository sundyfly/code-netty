package com.sundy.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 14:32:59
 * 描述：
 */
public class ProtocolClient {

    private String host;
    private int port;

    /*最大帧长度*/
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    /*字段长度*/
    private static final int LENGTH_FIELD_LENGTH = 4;
    /*字段偏移量*/
    private static final int LENGTH_FIELD_OFFSET = 6;
    /*长度调整*/
    private static final int LENGTH_ADJUSTMENT = 0;
    /*最初的字节地带*/
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public ProtocolClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            "decoder",
                            new ProtocolDecoder(MAX_FRAME_LENGTH,
                                    LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH,
                                    LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
                    ch.pipeline().addLast("encoder", new ProtocolEncoder());
                    ch.pipeline().addLast(new ProtocolClientHandler());

                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            int times = 0;
            while (times<10) {
            times++;
                // 发送消息给服务器
                ProtocolMessage msg = new ProtocolMessage();
                msg.setMagic((byte) 0x81);
                msg.setMsgType((byte) 0x01);
                msg.setReserve((short) 0);
                msg.setSn((short) 0);
                String body = "        静夜思     \n床前明月光，疑是地上霜。\n举头望明月，低头思故乡。\n";

                byte[] bodyBytes = body.getBytes(
                        Charset.forName("utf-8"));
                int bodySize = bodyBytes.length;
                msg.setLen(bodySize);

                msg.setBody(body);

                f.channel().writeAndFlush(msg);
                Thread.sleep(100);
            }
            // 等待连接关闭
            // f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ProtocolClient("localhost", 8082).run();
    }
}
