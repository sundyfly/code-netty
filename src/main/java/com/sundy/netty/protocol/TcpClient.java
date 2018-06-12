package com.sundy.netty.protocol;

import com.sundy.netty.util.ByteUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月31日 9:41:02
 * 描述：
 */
public class TcpClient {
    public static void main(String[] args) {
        Socket socket = null;
        OutputStream out = null;
        try {
            socket = new Socket("localhost", 8082);
            out = socket.getOutputStream();
            for (int i = 0; i < 1000; i++) {
            byte[] bys = setHeader("当前时间戳："+Long.toString(System.currentTimeMillis()));
                System.out.println(ByteUtils.covertByteToHex(bys));
                out.write(bys);
                out.flush();
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static byte[] setHeader(String body){
        byte[] bytes = null;
        try {
            bytes = body.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[bytes.length+10];
        buffer[0] = (byte) 0x81;
        buffer[1] = (byte) 0x01;
        buffer[2] = (byte) 0x00;
        buffer[3] = (byte) 0x00;

        buffer[4] = (byte) 0x00;
        buffer[5] = (byte) 0x00;

        setLength(buffer,bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            buffer[10+i] = bytes[i];
        }
        return buffer;
    }

    private static void setLength(byte[] buffer, int length) {
        buffer[9] = (byte) (length & 0xFF);
        buffer[8] = (byte) (length >> 8 & 0xFF);
        buffer[7] = (byte) (length >> 16 & 0xFF);
        buffer[6] = (byte) (length >> 24 & 0xFF);
    }

    public static String readFileToString(){
        File file = new File("D:\\idea\\code-netty\\src\\main\\java\\com\\sundy\\netty\\protocol\\ProtocolMessage.java");
        FileInputStream fis = null;
        try {
            StringBuilder sb = new StringBuilder();
             fis = new FileInputStream(file);
            System.out.println(file.length());
            BufferedReader  reader  = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line=reader.readLine())!=null){
                sb.append(line+"\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fis!=null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
