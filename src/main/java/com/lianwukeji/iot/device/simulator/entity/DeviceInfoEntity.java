package com.lianwukeji.iot.device.simulator.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-16
 * @since Jdk 1.8
 */
@Data
public class DeviceInfoEntity {
    private String did;
    private String type;
    private Integer asl;
    private Map<String,Object> authInfo;
    private String commProtocol;
    private String addr;
}
