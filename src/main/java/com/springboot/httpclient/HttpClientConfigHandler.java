package com.springboot.httpclient;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.util.Map;

/**
 * 客户端配置
 *
 * @author 林锋
 * @email 904303298@qq.com
 * @create 2018-04-03 11:02
 **/
public class HttpClientConfigHandler {

    /**
     * 配置常量
     */
    interface Constants {
        int MAX_TOTAL = 500;

        int MAX_ROUTE = 100;

        int TIME_OUT = 3;
    }

    /**
     * 有证书的部分编码暂时没有完成
     *
     * @return
     * @throws Exception
     */
    public static PoolingHttpClientConnectionManager createManager() throws Exception {
        return createManager(Constants.MAX_TOTAL, Constants.MAX_ROUTE);
    }

    /**
     * 有证书的部分编码暂时没有完成
     *
     * @param maxTotal
     * @param maxRoute
     * @return
     * @throws Exception
     */
    public static PoolingHttpClientConnectionManager createManager(int maxTotal, int maxRoute) throws Exception {
        // 添加https支持
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        manager.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        manager.setDefaultMaxPerRoute(maxRoute);

        return manager;
    }

    /**
     * 构建请求配置
     *
     * @return
     */
    public static RequestConfig createConfig() {
        return createConfig(Constants.TIME_OUT);
    }

    /**
     * 构建请求配置
     *
     * @param timeOut
     * @return
     */
    public static RequestConfig createConfig(int timeOut) {
        return createConfig(timeOut, timeOut, timeOut);
    }

    /**
     * 构建请求配置
     *
     * @param requestTimeOut
     * @param connectionTimeOut
     * @param socketTimeOut
     * @return
     */
    public static RequestConfig createConfig(int requestTimeOut, int connectionTimeOut, int socketTimeOut) {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(requestTimeOut * 1000)
                .setConnectTimeout(connectionTimeOut * 1000)
                .setSocketTimeout(socketTimeOut * 1000).build();

        return config;
    }

    /**
     * 构建请求头
     *
     * @param params
     * @return
     */
    public static Header[] createHeaders(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        Header[] headers = new Header[params.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return headers;
    }

    /**
     * 构建请求客户端
     *
     * @return
     */
    public static synchronized CloseableHttpClient createClient() throws Exception {
        return createClient(createManager(), createConfig());
    }

    /**
     * 构建请求客户端
     *
     * @param manager
     * @param config
     * @return
     */
    public static synchronized CloseableHttpClient createClient(PoolingHttpClientConnectionManager manager, RequestConfig config) {
        return HttpClients.custom()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(config)
                .setRetryHandler(new HttpClientRetryHandler())
                .build();
    }

}
