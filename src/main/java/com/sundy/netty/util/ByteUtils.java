package com.sundy.netty.util;

import java.io.ByteArrayOutputStream;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月31日 16:11:10
 * 描述：
 */
public class ByteUtils {
    private static final String HEX_STR = "0123456789abcdefABCDEF";
    /**
     * 将int转换为4个字节byte[]数组
     */
    public static byte[] convertByte(int i){
        byte[] ret = new byte[4];
        ret[0] = (byte) ((i >> 24) & 0xff);
        ret[1] = (byte) ((i >> 16) & 0xff);
        ret[2] = (byte) ((i >> 8) & 0xff);
        ret[3] = (byte) (i & 0xff);
        return ret;
    }

    /**
     * 把Int转成16进制的字符串, 默认转换出来的字符串全部为小写
     */
    public static String covertIntToHex(int i){
        return Integer.toHexString((byte) ((i >> 24) & 0xff)&0xff)
                + Integer.toHexString((byte) ((i >> 16) & 0xff)&0xff)
                + Integer.toHexString((byte) ((i >> 8) & 0xff)&0xff)
                + Integer.toHexString((byte) ((i  & 0xff)&0xff));
    }

    /**
     * 将byte[]数据转换为16进制的字符串，如byte[]{0x11, 0xaa, 0xbb, 0xcc}转换成"11aabbcc"
     * 默认转换出来的字符串全部为小写
     *
     * @param src  byte数组
     * @return hex字符串
     */
    public static String covertByteToHex(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = src.length; i < len; i++) {
            builder.append(String.format("%02x", src[i]));
        }
        return builder.toString();
    }

    /**
     * 将byte[]数据转换为16进制的字符串，如byte[]{0x11, 0xaa, 0xbb, 0xcc}转换成"11aabbcc"
     * 默认转换出来的字符串全部为小写 从开始位置到结束位置
     *
     * @param src  byte数组
     * @param start 开始位置
     * @param end  结束位置
     * @return hex字符串
     */
    public static String covertByteToHex(byte[] src, int start, int end) {
        if (src == null || src.length == 0) {
            return null;
        }
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (start < 0 || end > src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            builder.append(String.format("%02x", src[i]));
        }
        return builder.toString();
    }

    /**
     * 将一个int值的前byteLength个字节转换为十六进制的字符，比如0xab转换成"ab"
     *
     * @param src
     *            要转换的int值
     * @param byteLength
     *            转换前几个字节，范围[1,4]，否则会返回null值
     * @return hex字符串
     */
    public static String covertByteToHex(int src, int byteLength) {
        switch (byteLength) {
            case 1:
                return String.format("%02x", src & 0xff);
            case 2:
                return String.format("%04x", src & 0xffff);
            case 3:
                return String.format("%06x", src & 0xffffff);
            case 4:
                return String.format("%08x", src);
        }
        return null;
    }

    /**
     * 将16进制的字符串转换为byte[]，如"11aabbcc"转换成byte[]{0x11, 0xaa, 0xbb, 0xcc}
     * 函数会过滤字符串中不合法的字符
     *
     * @param hex
     *            16进制的字符串，不区分大小写
     * @return byte数组结果
     */
    public static byte[] covertHexToByte(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = hex.length(); i < len; i++) {
            char c = hex.charAt(i);
            if (HEX_STR.indexOf(c) != -1) {
                builder.append(c);
            }
        }
        String hexSrc = builder.toString();
        int strLength = hexSrc.length();
        byte[] result = new byte[strLength / 2];
        for (int i = 0, len = strLength / 2; i < len; i++) {
            result[i] = (byte) Integer.parseInt(hexSrc.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    /**
     * 将short转成2个字节byte[]
     * 如(short)0xabcd转换成byte[]{0xab, 0xcd}
     *
     * @param s short数据
     * @return 转换后的两个字节byte数组
     */
    public static byte[] covertShortToByte(short s) {
        byte[] ret = new byte[2];
        ret[0] = (byte) ((s >> 8) & 0xff);
        ret[1] = (byte) (s & 0xff);
        return ret;
    }

    /**
     * 把两个byte字节转换成short
     */
    public static short covertByteToShort(byte b1, byte b2) {
        short s1 = (short) ((b1 & 0xff) << 8);
        short s2 = (short) (b2 & 0xff);
        return (short) (s1 | s2);
    }

    /**
     * 将四个byte组合成一个int值
     */
    public static int covertByteToInt(byte b1, byte b2, byte b3, byte b4) {
        return (((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | (b4 & 0xff));
    }

    /**
     * 将多个参数组合成一个byte[]
     * 现在支持String、Byte、Short、Integer、Long、byte[]
     * <b>注意：Byte、Short、Integer、Long都会被当做byte处理</b>
     *
     * @param args
     *            可以包括byte、int、short、long、byte[]、String
     * @return byte数组结果
     */
    public static byte[] combine(Object... args) {
        if (args == null || args.length == 0) {
            return null;
        }
        ByteArrayOutputStream baas = new ByteArrayOutputStream(512);
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            try {
                if (arg instanceof String) {
                    byte[] beard = covertHexToByte((String) arg);
                    if (beard != null) {
                        baas.write(beard);
                    }
                } else if (arg instanceof byte[]) {
                    baas.write((byte[]) arg);
                } else if (arg instanceof Byte) {
                    baas.write((Byte) arg & 0xff);
                } else if (arg instanceof Short) {
                    baas.write((Short) arg & 0xff);
                } else if (arg instanceof Integer) {
                    baas.write((Integer) arg & 0xff);
                } else if (arg instanceof Long) {
                    baas.write((int) ((Long) arg & 0xff));
                } else {
                    throw new IllegalArgumentException("不被支持的参数[" + arg + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baas.toByteArray();
    }

}
