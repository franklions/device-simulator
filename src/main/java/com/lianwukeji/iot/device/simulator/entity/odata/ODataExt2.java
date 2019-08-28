package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-10
 * @since Jdk 1.8
 */
@Data
public class ODataExt2 extends OData {
    private String packets;
    /**
     * 包数
     */
    private Integer packetSize;

    @Override
    public void fill(ByteOp byteOp) {
        byteOp.putString(this.getDid());
        byteOp.put(this.getChannel());
        byteOp.put2BytesInt(this.getDeviceType(), true);
        byteOp.put1ByteInt(packetSize);
        byteOp.put(packets.getBytes());

    }

    @Override
    public int length() {
        return this.getDid().getBytes().length /** id **/ + 1 /** slot **/ + 2 /** device type **/ + 1 /** package number **/ + packets.getBytes().length ;
    }
}
