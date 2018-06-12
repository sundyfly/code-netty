package com.sundy.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 14:09:01
 * 描述：编码器
 */
public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMessage> {
    private static final String ENCODE = "UTF-8";
    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessage msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new Exception("The encode message is null");
        }else {
            out.writeByte(msg.getMagic());
            out.writeByte(msg.getMsgType());
            out.writeShort(msg.getReserve());
            out.writeShort(msg.getSn());
            String body = msg.getBody();
            if(body!=null){
                byte[] bytes = body.getBytes(Charset.forName(ENCODE));
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            }else {
                throw new Exception("The encode message body is null");
            }
        }
    }
}
