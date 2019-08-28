package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

/**
 * @author caozheng
 * @time 2019-05-09 10:20
 **/
@Data
public class OPacket implements BinaryData{
    private int dataType;
    private int dataVersion;
    private BinaryData data;

    @Override
    public void fill(ByteOp byteOp) {
        byteOp.put2BytesInt(dataType, true);
        byteOp.put1ByteInt(dataVersion);
        byteOp.put2BytesInt(data.length(), true);
        data.fill(byteOp);
    }

    @Override
    public int length() {
        return 2 /** data type **/ + 1 /** data version **/ + 2 /** data length **/ + data.length();
    }
}
