package com.lianwukeji.iot.device.simulator.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author caozheng
 * @time 2019-05-09 21:09
 **/
public class ByteOp {
    private final ByteBuffer buf;

    private ByteOp(byte[] bytes){
        this.buf = ByteBuffer.wrap(bytes);
    }

    private ByteOp(ByteBuffer buf){
        this.buf = buf;
    }

    public ByteOp slice(){
        return new ByteOp(this.buf.slice());
    }

    public ByteOp slice(int length){
        ByteBuffer thatBuf = this.buf.slice();
        thatBuf.limit(length);

        return new ByteOp(thatBuf);
    }

    public int position(){
        return this.buf.position();
    }

    public static ByteOp fromByteArray(byte[] bytes){
        return new ByteOp(bytes);
    }

    public static ByteOp allocate(int capacity) {
        return new ByteOp(ByteBuffer.allocate(capacity));
    }

    public ByteOp skip(int count){
        buf.position(buf.position() + count);

        return this;
    }

    public ByteOp expand(int count){
        buf.limit(buf.limit() + count);

        return this;
    }

    public byte peek(int offset){
        return buf.get(offset);
    }

    public byte get(){
        return buf.get();
    }

    public int getIntFrom1Byte(){
        return buf.get() & 0xFF;
    }

    public int getIntFrom4Bytes(boolean bigEndian){
        byte[] bytes = new byte[4];
        buf.get(bytes);
        if(bigEndian){
            return bytes[3] & 0xFF |
                    (bytes[2] & 0xFF) << 8 |
                    (bytes[1] & 0xFF) << 16 |
                    (bytes[0] & 0xFF) << 24;
        }else{
            return bytes[0] & 0xFF |
                    (bytes[1] & 0xFF) << 8 |
                    (bytes[2] & 0xFF) << 16 |
                    (bytes[3] & 0xFF) << 24;
        }
    }

    public int getIntFrom2Bytes(boolean bigEndian){
        byte[] bytes = new byte[2];
        buf.get(bytes);
        if(bigEndian){
            return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
        }else{
            return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8;
        }
    }

    public byte[] getBytes(int length){
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++){
            bytes[i] = buf.get();
        }

        return bytes;
    }

    public String getString(int length){
        return new String(getBytes(length));
    }

    public ByteOp put(byte value){
        buf.put(value);

        return this;
    }

    public ByteOp put(byte[] bytes){
        for(byte value : bytes){
            buf.put(value);
        }

        return this;
    }


    public ByteOp put(ByteOp byteOp){
        while(!byteOp.isEOF()){
            buf.put(byteOp.get());
        }

        return this;
    }

    public ByteOp put1ByteInt(int value){
        buf.put((byte)(value & 0xFF));

        return this;
    }

    public ByteOp put4BytesInt(int value, boolean bigEndian){
        byte[] bytes = new byte[4];
        if(bigEndian){
            bytes[0] = (byte)((value >> 24) & 0xFF);
            bytes[1] = (byte)(value >> 16 & 0xFF);
            bytes[2] = (byte)(value >> 8 & 0xFF);
            bytes[3] = (byte)(value & 0xFF);
        }else{
            bytes[0] = (byte)(value & 0xFF);
            bytes[1] = (byte)((value >> 8) & 0xFF);
            bytes[2] = (byte)((value >> 16) & 0xFF);
            bytes[3] = (byte)((value >> 24) & 0xFF);
        }

        buf.put(bytes);

        return this;
    }

    public ByteOp put2BytesInt(int value, boolean bigEndian){
        byte[] bytes = new byte[2];
        if(bigEndian){
            bytes[0] = (byte)((value >> 8) & 0xFF);
            bytes[1] = (byte)(value & 0xFF);
        }else{
            bytes[0] = (byte)(value & 0xFF);
            bytes[1] = (byte)((value >> 8) & 0xFF);
        }

        buf.put(bytes);

        return this;
    }

    public ByteOp putString(String str){
        buf.put(str.getBytes());

        return this;
    }

    public boolean isEOF(){
        return !buf.hasRemaining();
    }

    public int remain(){
        return buf.remaining();
    }

    public ByteOp flip(){
        buf.flip();

        return this;
    }

    public String toBase64(){
        Base64.Encoder encoder = Base64.getEncoder();
        ByteBuffer byteBuffer = encoder.encode(buf);
        CharBuffer charBuffer = Charset.forName("utf8").decode(byteBuffer);

        return charBuffer.toString();
    }

    public String toHex(){
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        while(buf.hasRemaining()){
            strHex = Integer.toHexString(buf.get() & 0xFF);
            if(strHex.length() == 1){
                sb.append("0");
            }
            sb.append(strHex);
        }

        return sb.toString();
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[buf.remaining()];
        int i = 0;
        while(buf.hasRemaining()){
            bytes[i++] = buf.get();
        }

        return bytes;
    }
}
