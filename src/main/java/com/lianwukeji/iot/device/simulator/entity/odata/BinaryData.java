package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;

/**
 * @author caozheng
 * @time 2019-08-08 09:40
 **/
public interface BinaryData {
    void fill(ByteOp byteOp);
    int length();
}
