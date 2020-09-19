package com.sample.agent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author aster
 */
public class AgentTest {
    public static void main(String[] args) {
        String url = "https://www.cctv.com/special/guanyunew/PAGE1381887116691173/index.shtml";
        try {
            RequestConfig requestConfig =
                RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(1000).build();
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;

            HttpGet method = new HttpGet(url);
            method.setConfig(requestConfig);
            method.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36");
            method.setHeader("Referer", url);

            //
            System.out.println("===开始构造HttpClient===");
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            System.out.println("===HttpClient构造完成===");
            response = httpClient.execute(method);

            HttpEntity repEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String content = EntityUtils.toString(repEntity, "UTF-8");
                //System.out.println(content);
            }
        } catch (IOException e) {
            System.out.println("url parse error:" + e.getMessage());
        }
    }
}
