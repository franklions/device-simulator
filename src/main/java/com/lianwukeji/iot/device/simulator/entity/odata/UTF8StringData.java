package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;

/**
 * @author caozheng
 * @time 2019-08-08 13:49
 **/
public class UTF8StringData implements BinaryData{
    private final String str;

    public UTF8StringData(String str) {
        this.str = str;
    }

    @Override
    public void fill(ByteOp byteOp) {
        byteOp.put(str.getBytes());
    }

    @Override
    public int length() {
        return str.getBytes().length;
    }

    @Override
    public String toString() {
        return str;
    }
}
