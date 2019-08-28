package com.lianwukeji.iot.device.simulator.entity.odata;

import com.lianwukeji.iot.device.simulator.utils.ByteUtils;
import lombok.Data;

import java.util.Base64;

/**
 * @author caozheng
 * @time 2019-05-09 10:18
 **/
@Data
public class ODataMessage {
    private String appid;
    private String apid;
    private String did;
    private Integer deviceType;
    private Integer packetNum;
    private String packetData;
    private long ts;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[appid: ").append(appid).append(", apid:").append(apid).append(", did: ").
                append(did).append(", deviceType: ").append(ByteUtils.to2BytesHex(deviceType))
                .append(", packetNum: ").append(packetNum).append(", ts: ").append(ts).append(", packetData: ")
                .append(packetData).append(", hex: ").append(ByteUtils.bytesToHex(Base64.getDecoder().decode(packetData))).append(")]");

        return sb.toString();
    }
}
