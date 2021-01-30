package io.github.jiarus.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author zhangjiaru
 * @date: 2021/01/25
 */
public class HttpClientUtil {
    
    public static CloseableHttpClient httpclient = HttpClients.createDefault();
    
    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return getHttpResult(httpGet);
    }
    
    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static String doPost(String url) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        return getHttpResult(httpPost);
    }
    
    private static String getHttpResult(HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }
}
