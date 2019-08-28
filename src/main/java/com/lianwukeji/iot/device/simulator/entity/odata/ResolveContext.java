package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

/**
 * @author caozheng
 * @time 2019-05-09 10:31
 **/
@Data
public class ResolveContext {
    private int dataType;
    private int dataVersion;
    private int dataLength;
    private ByteOp binData;
}
