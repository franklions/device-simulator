package com.lianwukeji.iot.device.simulator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lianwukeji.iot.device.simulator.entity.GatewayResult;
import com.lianwukeji.iot.device.simulator.entity.RdsAddrEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-15
 * @since Jdk 1.8
 */
public class RdsService {
    private static final Logger logger = LoggerFactory.getLogger(RdsService.class);
    private HttpClientTemplate httpTemplate ;
    private String uri;
    public RdsService(String uri, HttpClientTemplate instance) {
        if(uri == null || uri.isEmpty()){
            throw new NullPointerException("RdsUri is null");
        }
        this.uri = uri;
        this.httpTemplate = instance;
    }

    public String getAddrs(String name) {
        String result = this.httpTemplate.doGet(this.uri+"/addrs?names="+name);
        logger.info("根据RDS服务获取<{}>地址:{}",name,result);
        GatewayResult<List<RdsAddrEntity>> gwResult = JSON.parseObject(result,new TypeReference<GatewayResult<List<RdsAddrEntity>>>() {});
        if(gwResult.getResult().equals(1)){
            return gwResult.getReturnValue().get(0).getAddrs().get(0);
        }
        return null;
    }
}
