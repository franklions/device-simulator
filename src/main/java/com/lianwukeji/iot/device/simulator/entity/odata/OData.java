package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caozheng
 * @time 2019-05-09 10:19
 **/
@Data
public class OData implements BinaryData{
    private String did;
    private byte channel;
    private Integer deviceType;

    private List<OPacket> packetList = new ArrayList<>();

    @Override
    public void fill(ByteOp byteOp) {
        byteOp.putString(did);
        byteOp.put(channel);
        byteOp.put2BytesInt(deviceType, true);
        byteOp.put1ByteInt(packetList.size());

        for(OPacket p : packetList){
            p.fill(byteOp);
        }
    }

    @Override
    public int length() {
        return did.getBytes().length /** id **/ + 1 /** slot **/ + 2 /** device type **/ + 1 /** package number **/
                + packetList.stream().map(p -> p.length()).reduce((a, b) -> a + b).orElseGet(() -> 0);
    }

    public ByteOp toBytes(){
        ByteOp byteOp = ByteOp.allocate(this.length());
        this.fill(byteOp);
        byteOp.flip();

        return byteOp;
    }
}
