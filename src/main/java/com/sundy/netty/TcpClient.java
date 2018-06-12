package com.sundy.netty;

import com.sundy.netty.util.ByteUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 16:32:11
 * 描述：
 */
public class TcpClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = null;
        OutputStream out = null;

        try {
            socket = new Socket("localhost", 8082);
            out = socket.getOutputStream();
            byte[] bys = null;
            for (int i = 0; i < 1000; i++) {

                bys = new byte[20];
                bys[0] = (byte) 0x81;
                bys[1] = (byte) 0x01;
                bys[2] = (byte) 0x00;
                bys[3] = (byte) 0x00;

                bys[4] = (byte) 0x00;
                bys[5] = (byte) 0x00;

                bys[6] = (byte) 0x00;
                bys[7] = (byte) 0x00;
                bys[8] = (byte) 0x00;
                bys[9] = (byte) 0x0A;

                String s = "我们的W";
                byte[] bytes = s.getBytes("utf-8");
                for (int j = 0; j < bytes.length; j++) {
                    bys[10 + j] = bytes[j];
                }
                System.out.println(ByteUtils.covertByteToHex(bys));
                out.write(bys);
                out.flush();
                socket.setKeepAlive(true);
                try {
                    Thread.sleep(1250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 请求服务器


        }finally {
            Thread.sleep(15000);
            out.close();
            socket.close();
        }
    }
}