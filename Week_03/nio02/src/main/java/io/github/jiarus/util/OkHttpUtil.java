package io.github.jiarus.util;


import okhttp3.*;

import java.io.IOException;

/**
 * @author jiarus
 * @date: 2021/01/22
 */
public class OkHttpUtil {
    
    public static OkHttpClient client = new OkHttpClient();
    
    /**
     * get
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static String doGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    /**
     * post
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static String doPost(String url, String content) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), content);
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        
        String result = doGet("http://localhost:8801");
        System.out.println("get method result:" + result);
        String result2 = doPost("http://localhost:8801", "{\"key\",\"hello\"}");
        System.out.println("post method result:" + result2);
    }
}
