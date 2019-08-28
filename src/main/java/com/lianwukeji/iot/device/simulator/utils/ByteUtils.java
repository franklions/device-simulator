package com.lianwukeji.iot.device.simulator.utils;

/**
 * @author caozheng
 * @time 2019-05-09 11:46
 **/
public class ByteUtils {
    public static String to2BytesHex(int value){
        String hexStr = "0000"+String.format("%02X", value);
        return hexStr.substring(hexStr.length()-4);
    }
    public static int getIntFrom1Bytes(byte[] body, int pos){
        if(body == null){
            throw new IllegalArgumentException("body");
        }

        return body[pos] & 0xFF;
    }
    public static int getIntFrom2Bytes(byte[] body, int pos, boolean bigEndian){
        if(body == null){
            throw new IllegalArgumentException("body");
        }
        if(bigEndian){
            return body[pos + 1] & 0xFF | (body[pos] & 0xFF) << 8;
        }else{
            return body[pos] & 0xFF | (body[pos + 1] & 0xFF) << 8;
        }
    }

    public static int getIntFrom4Bytes(byte[] body, int pos, boolean bigEndian){
        if(body == null){
            throw new IllegalArgumentException("body");
        }
        if(bigEndian){
            return body[pos + 3] & 0xFF |
                    (body[pos + 2] & 0xFF) << 8 |
                    (body[pos + 1] & 0xFF) << 16 |
                    (body[pos] & 0xFF) << 24;
        }else{
            return body[pos] & 0xFF |
                    (body[pos + 1] & 0xFF) << 8 |
                    (body[pos + 2] & 0xFF) << 16 |
                    (body[pos + 3] & 0xFF) << 24;
        }
    }

    public static byte[] hexToBytes(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }

    public static String bytesToHex(byte[] bytes){
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }
}
