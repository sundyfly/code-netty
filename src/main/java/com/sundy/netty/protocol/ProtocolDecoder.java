package com.sundy.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 11:15:35
 * 描述：解码器
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {
    private static final int HEADER_SIZE = 10;
    private static final String ENCODE = "UTF-8";
    private byte magic; 	// 魔数
    private byte msgType;	// 消息类型
    private short reserve;	// 保留字
    private short sn;		// 序列号
    private int len;		// 长度
    private String body;    //消息体

    public ProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
                lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected ProtocolMessage decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, byteBuf);
        if (in == null) {
            return null;
        }

//        if (in.readableBytes() < HEADER_SIZE) {
//            return null;
//        }


        ProtocolMessage message = new ProtocolMessage();
        magic = in.readByte();
        msgType = in.readByte();
        reserve = in.readShort();
        sn = in.readShort();

        if(magic==(byte) 0x81||magic==(byte)0x80){//只判断协议头为0x81或者0x80的协议，其他全部抛出异常
            message.setMagic(magic);
            message.setMsgType(msgType);
            message.setReserve(reserve);
            message.setSn(sn);
            int len = in.readInt();
            if(in.readableBytes()<len){
                return  null;
            }else {
                ByteBuf buf = in.readBytes(len);
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                message.setBody(new String(req, ENCODE));
            }
        }else{
            System.out.println("in.readableBytes()= "+in.readableBytes()+"  magic= "+magic);
            throw new Exception("客户端发送的消息协议不正确...");
        }
        return message;
    }
}
