package com.lianwukeji.iot.device.simulator.entity;

import lombok.Data;

import java.util.List;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-16
 * @since Jdk 1.8
 */
@Data
public class GroupListEntity {
    private String groupid;
    private List<DeviceInfoEntity> devices;
}
