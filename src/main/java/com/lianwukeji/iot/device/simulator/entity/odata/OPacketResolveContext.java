package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

/**
 * @author caozheng
 * @time 2019-08-07 15:12
 **/
@Data
public class OPacketResolveContext {
    private int dataType;
    private int dataVersion;
    private int dataLength;
    private ByteOp binData;
}
