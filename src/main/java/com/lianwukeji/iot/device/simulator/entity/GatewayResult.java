package com.lianwukeji.iot.device.simulator.entity;

import lombok.Data;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-15
 * @since Jdk 1.8
 */
@Data
public class GatewayResult<T> {
    private Integer result;
    private Long ts;
    private Double code;
    private String msg;
    private  T returnValue;
}
