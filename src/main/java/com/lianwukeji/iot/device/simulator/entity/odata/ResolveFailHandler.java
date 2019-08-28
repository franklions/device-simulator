package com.lianwukeji.iot.device.simulator.entity.odata;

/**
 * @author caozheng
 * @time 2019-05-09 10:38
 **/
public interface ResolveFailHandler {
    boolean handleFail(ODataMessage dto, ResolveContext context, ResolveResult result);
}
