package com.lianwukeji.iot.device.simulator.service;

import com.lianwukeji.iot.device.simulator.entity.GatewayResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-15
 * @since Jdk 1.8
 */
public class HttpClientTemplate {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientTemplate.class);

    private static HttpClient client4HTTP;

    private static HttpClient client4HTTPS;

    private static final   HttpClientTemplate instance = new HttpClientTemplate();

    private HttpClientTemplate(){
        client4HTTP = HttpClientBuilder.create().build();
    }

    public static HttpClientTemplate getInstance() {
        return instance;
    }

    public String doGet(String uri) {
        logger.info("doGet uri={}",uri);
        String body= "";
        if(uri.startsWith("https://")){
            logger.info("https request");
        }else{
            logger.info("http");
            HttpGet request = new HttpGet(uri);
            HttpResponse response = null;
            try {
                response = client4HTTP.execute(request);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    body = EntityUtils.toString(response.getEntity(), "utf-8");
                }else{
                    String error =  EntityUtils.toString(response.getEntity(), "utf-8");
                    logger.error("HttpStatus={},body={}",response.getStatusLine().getStatusCode(),error);
                }
            } catch (IOException e) {
                logger.error("HttpClient doGet异常:",e);
            }

        }
        return body;
    }

    public String doPost(String uri, Map<String,String> map) {

        HttpPost httpPost = new HttpPost(uri);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        try {
            httpPost.setEntity( new UrlEncodedFormEntity(nvps, "utf-8"));

            httpPost.setHeader("Content-type", "application/json");

            //执行请求操作，并拿到结果（同步阻塞）
            HttpResponse response = null;

            response = client4HTTP.execute(httpPost);
            //获取结果实体
            String body = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "utf-8");
            }

            return body;
        } catch (IOException e) {
            logger.error("HttpClient doPost Map异常:",e);
        }

        return null;
    }

    public String doPost(String uri, String json) {

        HttpPost httpPost = new HttpPost(uri);


        try {
            httpPost.setEntity( new StringEntity(json, "utf-8"));
            httpPost.setHeader("Content-type", "application/json");

            //执行请求操作，并拿到结果（同步阻塞）
            HttpResponse response = null;

            response = client4HTTP.execute(httpPost);
            //获取结果实体
            String body = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "utf-8");
            }

            return body;
        } catch (IOException e) {
            logger.error("HttpClient doPost Json异常:",e);
        }

        return null;
    }
}
