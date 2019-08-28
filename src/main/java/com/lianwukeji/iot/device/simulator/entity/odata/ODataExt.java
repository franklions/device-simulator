package com.lianwukeji.iot.device.simulator.entity.odata;

import lombok.Data;

/**
 * @author caozheng
 * @time 2019-08-08 09:18
 **/
@Data
public class ODataExt extends OData {
    private String appid;
    private String apid;
    private long ts;
}
