package com.lianwukeji.iot.device.simulator.entity.odata;

/**
 * @author caozheng
 * @time 2019-08-07 15:09
 **/
public interface OPacketResolver {
    ResolveResult resolve(OPacketResolveContext ctx);
}
