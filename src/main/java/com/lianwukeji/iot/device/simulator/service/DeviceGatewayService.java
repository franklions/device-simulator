package com.lianwukeji.iot.device.simulator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lianwukeji.iot.device.simulator.entity.GatewayResult;
import com.lianwukeji.iot.device.simulator.entity.GroupListEntity;
import com.lianwukeji.iot.device.simulator.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-15
 * @since Jdk 1.8
 */
public class DeviceGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceGatewayService.class);

    private HttpClientTemplate httpTemplate;
    private String deviceGatewayAddr;

    private static String token =null;
    private static GroupListEntity groupList = null;

    private static String apDevice = null;
    private static String privateKey = null;

    public DeviceGatewayService(String addr,String did,String theKey) {
        apDevice = did;
        privateKey = theKey;
        if (addr.startsWith("https")) {
            this.deviceGatewayAddr = addr.replace("https", "http");
        } else {
            this.deviceGatewayAddr = addr;
        }
        this.httpTemplate = HttpClientTemplate.getInstance();

        //获取token
        getToken();
        //获取组名单
        initGroupList();

    }

    private void initGroupList() {
        if(token == null || token.isEmpty()){
            getToken();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("did", apDevice);
        body.put("token", token);

        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/group_list", JSON.toJSONString(body));
        logger.info("获取组名单 Response={}", response);
        GatewayResult<Object> result = JSON.parseObject(response, GatewayResult.class);
        if(result.getResult().equals(1)){
            if(result.getReturnValue() == null){
                groupList =null;
            }else{
                GatewayResult<GroupListEntity> entity = JSON.parseObject(response, new TypeReference<GatewayResult<GroupListEntity>>(){});
                groupList = entity.getReturnValue();
            }
        }
    }

    public GroupListEntity getGroupList()  {
        if(groupList == null){
             initGroupList();
        }

        return groupList;
    }

    public String getToken()  {

        //三步认证 S1
        Map<String, String> body1 = new HashMap<>();
        body1.put("did", apDevice);
        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/auth/s1", JSON.toJSONString(body1));
        logger.info("Auth S1={}", response);
        GatewayResult<Map<String, Object>> result1 = JSON.parseObject(response, GatewayResult.class);

        if(result1.getResult() != 1){
            throw new IllegalArgumentException(result1.getMsg());
        }

        String ticket = result1.getReturnValue().get("ticket").toString();

        //S2
        Map<String, String> body2 = new HashMap<>();
        body2.put("did", apDevice);
        body2.put("ticket", ticket);
        body2.put("algo", "MD5");
        String response2 = httpTemplate.doPost(this.deviceGatewayAddr + "/auth/s2", JSON.toJSONString(body2));
        logger.info("Auth S2={}", response2);
        GatewayResult<Map<String, Object>> result2 = JSON.parseObject(response2, GatewayResult.class);

        if(result2.getResult() != 1){
            throw new IllegalArgumentException(result1.getMsg());
        }

        String signature = null;
        try {
            signature = RSAUtils.sign(result2.getReturnValue().get("msgDigest").toString().getBytes(), privateKey, "MD5");
        } catch (Exception e) {
            logger.error("获取token时签名过程异常:",e);
        }
        //S3
        Map<String, String> body3 = new HashMap<>();
        body3.put("did", apDevice);
        body3.put("ticket", ticket);
        body3.put("signature", signature);
        String response3 = httpTemplate.doPost(this.deviceGatewayAddr + "/auth/s3", JSON.toJSONString(body3));
        logger.info("Auth S3={}", response3);
        GatewayResult<Map<String, Object>> result3 = JSON.parseObject(response3, GatewayResult.class);

        if(result3.getResult() != 1){
            throw new IllegalArgumentException(result1.getMsg());
        }

        token = result3.getReturnValue().get("token").toString();
        return token;
    }

    public String postData(String did, String data,String dataType,String sid){
        if(token == null || token.isEmpty()){
            getToken();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ap", apDevice);
        body.put("token", token);
        body.put("did", did);
        body.put("data", data);
        body.put("type", dataType);
        body.put("ts",System.currentTimeMillis());
        body.put("sid", sid);
        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/data", JSON.toJSONString(body));
        logger.info("Post Data Response={}", response);
        //GatewayResult<Object> result = JSON.parseObject(response, GatewayResult.class);

        return response;
    }

    public String postData2(String data){
        if(token == null || token.isEmpty()){
            getToken();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ap", apDevice);
        body.put("token", token);
        body.put("data", data);
        body.put("ts",System.currentTimeMillis());
        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/data2", JSON.toJSONString(body));
        logger.info("Post Data2 Response={}", response);
        //GatewayResult<Object> result = JSON.parseObject(response, GatewayResult.class);

        return response;
    }

    public String postJoin(String did) {
        if(token == null || token.isEmpty()){
            getToken();
        }
        List<Map<String,Object>> devices = new ArrayList<>();
        Map<String,Object> data =new HashMap<>();
        data.put("did",did);
        data.put("channel",1);

        devices.add(data);

        Map<String, Object> body = new HashMap<>();
        body.put("did", apDevice);
        body.put("token", token);
        body.put("devices", devices);
        body.put("ts",System.currentTimeMillis());
        String bodyStr = JSON.toJSONString(body);
        logger.info("Join body="+bodyStr);
        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/join",bodyStr );
        logger.info("Post Join Response={}", response);
       // GatewayResult<Object> result = JSON.parseObject(response, GatewayResult.class);

        return response;
    }

    public String postDisjoin(String did) {
        if(token == null || token.isEmpty()){
            getToken();
        }

        List<String> cps = new ArrayList<>();
        cps.add(did);

        Map<String, Object> body = new HashMap<>();
        body.put("did", apDevice);
        body.put("token", token);
        body.put("cps", cps);
        body.put("ts",System.currentTimeMillis());
        String response = httpTemplate.doPost(this.deviceGatewayAddr + "/disjoin", JSON.toJSONString(body));
        logger.info("Post Disjoin Response={}", response);
        // GatewayResult<Object> result = JSON.parseObject(response, GatewayResult.class);

        return response;
    }
}
