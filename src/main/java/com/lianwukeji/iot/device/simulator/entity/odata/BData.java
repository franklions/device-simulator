package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import lombok.Data;

/**
 * @author caozheng
 * @time 2019-08-08 17:29
 **/
@Data
public class BData implements BinaryData {
    private int version;
    private OData oData;

    @Override
    public void fill(ByteOp byteOp) {
        byteOp.put1ByteInt(version).put2BytesInt(oData.length(), true);
        oData.fill(byteOp);
    }

    @Override
    public int length() {
        return 1 /** version **/ + 2 /** odata length **/ + oData.length();
    }

    public ByteOp toBytes(){
        ByteOp byteOp = ByteOp.allocate(this.length());
        this.fill(byteOp);
        byteOp.flip();

        return byteOp;
    }
}
