package cn.oneao.noteclient.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author: HongZhi Wang
 * @Date: 2020/8/14 13:40
 */
public class HttpClientUtil {

    private static String tokenString = "";

    /**
     * 以get方式调用第三方接口
     */
    public static String doGet(String url,boolean isHttps) {
        String responseContent = "";
        //1.创建HttpClient对象
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            //这里我加了一个是否需要创建一个https连接的判断
            if (isHttps) {
                //配置https请求的一些参数
                SSLContext sslContext = SSLContextBuilder.create().useProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
                RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
                httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
            } else {
                httpClient = HttpClientBuilder.create().build();
            }
            //2.生成get请求对象，并设置请求头信息
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("auth_token", tokenString);
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            //3.执行请求
            response = httpClient.execute(httpGet);
            //4.处理响应信息
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseContent = EntityUtils.toString(response.getEntity(),"utf-8");
                return responseContent;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 以post方式调用第三方接口
     */
    public static String doPost(String url, boolean isHttps, JSONObject paramEntity) {
        String responseContent = "";
        //1.创建HttpClient对象
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            if (isHttps) {
                //配置https请求的一些参数
                SSLContext sslContext = SSLContextBuilder.create().useProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
                RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
                httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
            } else {
                httpClient = HttpClientBuilder.create().build();
            }
            //2.生成post请求对象，并设置请求头信息
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("auth_token", tokenString);
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            //3.设置请求参数
            if (!Objects.isNull(paramEntity)) {
                String paramStr = JSONObject.toJSONString(paramEntity);
                StringEntity entity = new StringEntity(paramStr,ContentType.create("application/json","utf-8"));
                httpPost.setEntity(entity);
            }
            //4.执行请求
            response = httpClient.execute(httpPost);
            //5.处理响应信息
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseContent = EntityUtils.toString(response.getEntity(),"utf-8");
                return responseContent;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }
}
