package com.sundy.netty.protocol;

import java.io.Serializable;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 13:48:04
 * 描述：消息对象
 */
public class ProtocolMessage implements Serializable {

    private static final long serialVersionUID = 8934671082177744279L;
    private byte magic; 	// 魔数
    private byte msgType;	// 消息类型
    private short reserve;	// 保留字
    private short sn;		// 序列号
    private int len;		// 长度
    private String body;    //消息体

    public byte getMagic() {
        return magic;
    }

    public ProtocolMessage setMagic(byte magic) {
        this.magic = magic;
        return this;
    }

    public byte getMsgType() {
        return msgType;
    }

    public ProtocolMessage setMsgType(byte msgType) {
        this.msgType = msgType;
        return this;
    }

    public short getReserve() {
        return reserve;
    }

    public ProtocolMessage setReserve(short reserve) {
        this.reserve = reserve;
        return this;
    }

    public short getSn() {
        return sn;
    }

    public ProtocolMessage setSn(short sn) {
        this.sn = sn;
        return this;
    }

    public int getLen() {
        return len;
    }

    public ProtocolMessage setLen(int len) {
        this.len = len;
        return this;
    }

    public String getBody() {
        return body;
    }

    public ProtocolMessage setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "ProtocolMessage{" +
                "magic=" + magic +
                ", msgType=" + msgType +
                ", reserve=" + reserve +
                ", sn=" + sn +
                ", len=" + len +
                ", body='" + body + '\'' +
                '}';
    }
}
