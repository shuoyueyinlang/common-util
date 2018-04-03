package com.springboot.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * http客户端工具类
 *
 * @author 林锋
 * @email 904303298@qq.com
 * @create 2018-04-02 14:07
 **/
public class HttpClientUtil {

    /**
     * 配置常量
     */
    interface Constants {
        String CHAR_SET_UTF_8 = "UTF-8";
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public static String get(String url, HashMap<String, Object> params) {
        return get(url, null, params);
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public static String get(String url, HashMap<String, String> headers, HashMap<String, Object> params) {
        HttpGet get = new HttpGet(String.format("%s%s", url, getQuery(params)));
        return execute(get, headers);
    }

    /**
     * post方式提交form表单
     *
     * @param url
     * @param params
     * @return
     */
    public static String postFormData(String url, HashMap<String, Object> params) {
        HashMap<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Disposition", "multipart/form-data;");
        return postForm(url, headers, params);
    }

    /**
     * post方式提交form表单
     *
     * @param url
     * @param params
     * @return
     */
    public static String postFormUrlencoded(String url, HashMap<String, Object> params) {
        HashMap<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/x-www-form-urlencoded;");
        return postForm(url, headers, params);
    }

    /**
     * post方式提交form表单
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static String postForm(String url, HashMap<String, String> headers, HashMap<String, Object> params) {
        HttpPost post = new HttpPost(String.format("%s%s", url, getQuery(params)));
        return execute(post, headers);
    }

    /**
     * post数据元数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static String postJson(String url, HashMap<String, String> headers, Object params) {
        return post(url, headers, params.toString());
    }

    /**
     * post数据元数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static String post(String url, HashMap<String, String> headers, String params) {
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(params, Constants.CHAR_SET_UTF_8));
        return execute(post, headers);
    }

    /**
     * 执行用户请求
     *
     * @param request
     * @param headers
     * @return
     */
    public static String execute(HttpUriRequest request, HashMap<String, String> headers) {
        return execute(request, headers, Constants.CHAR_SET_UTF_8);
    }

    /**
     * 执行用户请求
     *
     * @param request
     * @param headers
     * @param charset
     * @return
     */
    public static String execute(HttpUriRequest request, HashMap<String, String> headers, String charset) {
        try {
            CloseableHttpClient httpClient = HttpClientConfigHandler.createClient();
            if (headers != null) {
                request.setHeaders(HttpClientConfigHandler.createHeaders(headers));
            }
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取请求参数
     *
     * @param params
     * @return
     */
    private static String getQuery(HashMap<String, Object> params) {
        if (params != null && params.size() > 0) {
            boolean isFirst = true;
            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (isFirst) {
                    buffer.append("?" + entry.getKey() + "=" + entry.getValue());
                    isFirst = false;
                } else {
                    buffer.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
            return buffer.toString();
        }
        return "";
    }
}
