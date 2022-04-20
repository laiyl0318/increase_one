package com.jiayi.common.util;

import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.constants.CommonConstants;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Desc:    HTTP请求方式通用类
 *
 * @author wyq
 */
@Slf4j
public final class HttpClientUtil {
    /**
     * utf-8字符编码
     */
    private static final String CHARSET_UTF_8 = "utf-8";
    /**
     * HTTP内容类型,相当于form表单的形式，提交数据
     */
    private static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";
    /**
     * HTTP内容类型,相当json的形式，提交数据
     */
    private static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";
    /**
     * HTTP内容类型,相当于xml的形式,提交数据
     */
    private static final String CONTENT_TYPE_XML_URL = "text/xml;charset=utf-8";
    private final static int MAX_TOTAL = 500;
    private final static int MAX_PER_ROUTE = 50;
    /**
     * ms毫秒,读取超时时间
     */
    private final static int SOCKET_TIMEOUT = 30000;
    /**
     * ms毫秒,建立链接超时时间
     */
    private final static int CONNECT_TIMEOUT = 5000;
    /**
     * ms毫秒,从池中获取链接超时时间
     */
    private final static int CONNECTION_REQUEST_TIMEOUT = 5000;
    /**
     * 连接最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 2;
    /**
     * org.apache.http.impl.client.CloseableHttpClient
     */
    private static CloseableHttpClient httpclient = null;
    /**
     * 连接管理器
     */
    private static PoolingHttpClientConnectionManager pool;
    /**
     * 请求配置
     */
    private static RequestConfig requestConfig;
    /**
     * 设置重试规则
     */
    private static HttpRequestRetryHandler httpRequestRetryHandler;
    private HttpClientUtil() {
    }

    public static CloseableHttpClient getHttpClient() {
        if (null == httpclient) {
            synchronized (HttpClientUtil.class) {
                if (null == httpclient) {
                    httpclient = init();
                }
            }
        }
        return httpclient;
    }

    /**
     * 初始化连接池
     *
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient init() {
        return init(MAX_TOTAL, MAX_PER_ROUTE, CONNECTION_REQUEST_TIMEOUT, SOCKET_TIMEOUT, CONNECT_TIMEOUT);
    }

    /**
     * 初始化连接池
     *
     * @param maxConnTotal             客户端总并行链接最大数
     * @param defaultMaxPerRoute       每个主机的最大并行链接数
     * @param connectionRequestTimeout 链接请求最大超时时间
     * @param socketTimeout            读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
     * @param connectTimeout           连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient init(final int maxConnTotal, final int defaultMaxPerRoute, final int connectionRequestTimeout
            , final int socketTimeout, final int connectTimeout) {

        ConnectionSocketFactory plainsf;
        LayeredConnectionSocketFactory sslsf;
        // 下面是暂时添加的信任所有 https 请求，是最简单粗暴的解决办法；通常要请求的https服务端，应该在 nginx 服务器进行信任设置，或者服务端添加证书
        try {
            plainsf = PlainConnectionSocketFactory.getSocketFactory();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        } catch (Exception ex) {
            log.error("初始化http连接异常: {}", Arrays.toString(ex.getStackTrace()));
            return null;
        }

        // 配置同时支持 HTTP 和 HTTPS
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf).register("https", sslsf).build();
        // 初始化连接管理器
        pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 将最大连接数增加到1000，实际项目最好从配置文件中读取这个值
        pool.setMaxTotal(maxConnTotal);
        //增加路由最大连接数为100，与限流阀值rate.limit.count一致，解决Timeout waiting for connection from pool 问题
        // 设置最大路由
        pool.setDefaultMaxPerRoute(defaultMaxPerRoute);

        // 请求重试处理
        httpRequestRetryHandler = (exception, executionCount, context) -> {
            // 如果已经重试了2次，就放弃
            if (executionCount >= MAX_RETRY_COUNT) {
                return false;
            }
            // 如果服务器丢掉了连接，那么就重试
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            // 不要重试SSL握手异常
            if (exception instanceof SSLHandshakeException) {
                return false;
            }
            // 超时
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            // 目标服务器不可达
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // 连接被拒绝
            if (exception instanceof ConnectTimeoutException) {
                return false;
            }
            // SSL握手异常
            if (exception instanceof SSLException) {
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();

            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };


        // 根据默认超时限制初始化requestConfig
        // 设置请求超时时间
        requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();


        return HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(pool)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(httpRequestRetryHandler).build();

    }

    /**
     * 统一处理请求数据
     *
     * @param httpPost
     * @param httpGet
     * @return
     */
    private static CallResult<String> httpSend(HttpPost httpPost, HttpGet httpGet) {
        try {
            CloseableHttpResponse response;
            // 创建默认的httpClient实例.
            CloseableHttpClient httpClient = getHttpClient();
            if (!Objects.isNull(httpPost)) {
                httpPost.setConfig(requestConfig);
                // 执行请求
                response = httpClient.execute(httpPost);
            } else if (!Objects.isNull(httpGet)) {
                httpGet.setConfig(requestConfig);
                // 执行请求
                response = httpClient.execute(httpGet);
            } else {
                return CallResult.failure("无效的http请求");
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                return CallResult.success(EntityUtils.toString(response.getEntity(), CHARSET_UTF_8));
            } else {
                return CallResult.failure("http请求失败，状态码：" + statusCode);
            }
        } catch (Exception ex) {
            log.error("HttpClientUtil.httpSend error ", ex);
            return CallResult.failure(ex.getMessage());
        }
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl get请求url
     */
    public static CallResult<String> httpGet(String httpUrl) {
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        return httpSend(null, httpGet);
    }

    /**
     * 发送post请求，带参数
     * 格式：application/x-www-form-urlencoded
     *
     * @param httpUrl 地址
     * @param params  参数(格式:key1=value1&key2=value2)
     */
    public static CallResult<String> httpPost(String httpUrl, String params) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        // 设置参数
        if (!Strings.isNullOrEmpty(params)) {
            StringEntity stringEntity = new StringEntity(params, CHARSET_UTF_8);
            //form表单方式提交
            stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
            httpPost.setEntity(stringEntity);
        }
        return httpSend(httpPost, null);
    }

    /**
     * 带URLEncoded的POST提交形式
     *
     * @param httpUrl 请求地址
     * @param maps    参数
     * @param headers 头信息
     * @return BusinessResult
     */
    public static CallResult<String> httpPost(String httpUrl, TreeMap<String, String> maps, TreeMap<String, String> headers) {
        try {
            // 创建默认的httpClient实例.
            CloseableHttpClient httpClient = getHttpClient();
            // 创建httpPost
            HttpPost httpPost = new HttpPost(httpUrl);
            List<NameValuePair> list = maps.keySet().stream()
                    .map(k -> new BasicNameValuePair(k, maps.get(k))).collect(Collectors.toList());
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, CHARSET_UTF_8);
            httpPost.setEntity(entity);
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(httpPost::setHeader);
            }
            return httpSend(httpPost, null);
        } catch (Exception ex) {
            log.error("HttpClientUtil.sendHttpPost Invalid", ex);
            return CallResult.failure(ex.getMessage());
        }
    }

    /**
     * post请求  TreeMap 参数
     *
     * @param httpUrl
     * @param params
     * @return
     */
    public static CallResult<String> httpPost(String httpUrl, TreeMap<String, String> params) {
        return httpPost(httpUrl, params, null);
    }

    /**
     * 发送 post请求 发送json数据
     *
     * @param httpUrl    地址
     * @param paramsJson 参数(格式 json)
     */
    public static CallResult<String> sendHttpPostJson(String httpUrl, String paramsJson) {
        return sendHttpPostJson(httpUrl, paramsJson, null);
    }

    /**
     * 发送 post请求 发送json数据
     *
     * @param httpUrl    地址
     * @param paramsJson 参数(格式 json)
     */
    public static CallResult<String> sendHttpPostJson(String httpUrl, String paramsJson, TreeMap<String, String> headers) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        // 设置参数
        if (!Strings.isNullOrEmpty(paramsJson)) {
            StringEntity stringEntity = new StringEntity(paramsJson, CHARSET_UTF_8);
            //json格式提交数据
            stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
            httpPost.setEntity(stringEntity);
        }
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(httpPost::setHeader);
        }
        return httpSend(httpPost, null);
    }


    /**
     * 从服务器下载文件到本地
     *
     * @param resource String
     * @param fileName String
     * @return boolean
     */
    public static boolean download(String resource, String fileName) {
        //此方法只能用户HTTP协议
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            URL url = new URL(resource);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(new FileOutputStream(fileName));
            return download(in, out);
        } catch (Exception e) {
            log.trace("-------> download_file_failed1." + e.getMessage());
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.trace("-------> download_file_failed2." + ex.getMessage());
            }
        }
    }

    /**
     * 从服务器下载文件到本地
     *
     * @param resource String   资源地址
     * @param out      输出地址
     * @return boolean
     */
    public static boolean download(String resource, OutputStream out) {
        //此方法只能用户HTTP协议
        DataInputStream in = null;
        try {
            URL url = new URL(resource);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            in = new DataInputStream(connection.getInputStream());
            return download(in, out);
        } catch (Exception e) {
            log.trace("-------> download_file_failed1." + e.getMessage());
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.trace("-------> download_file_failed2." + ex.getMessage());
            }
        }
    }

    /**
     * 从服务器下载文件到本地
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @return boolean
     */
    public static boolean download(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, count);
        }
        return true;
    }

    /**
     * 发起 xml 请求
     *
     * @param url     String
     * @param xmlData String
     * @return BusinessResult
     */
    public static CallResult httpPostXml(String url, String xmlData) {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (xmlData != null && xmlData.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(xmlData, CHARSET_UTF_8);
                stringEntity.setContentType(CONTENT_TYPE_XML_URL);
                stringEntity.setContentEncoding(CHARSET_UTF_8);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception ex) {
            log.error("发送 xml 请求异常, 异常消息: {}", Arrays.toString(ex.getStackTrace()));
        }
        return httpSend(httpPost, null);
    }

    /**
     * 发起 xml 请求
     *
     * @param url     String
     * @param xmlData String
     * @return BusinessResult
     */
    public static CallResult<String> httpPostXmlWithXwww(String url, String xmlData) {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (xmlData != null && xmlData.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(xmlData, CHARSET_UTF_8);
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                stringEntity.setContentEncoding(CHARSET_UTF_8);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception ex) {
            log.error("发送 xml 请求异常, 异常消息: {}", Arrays.toString(ex.getStackTrace()));
            return CallResult.failure("xml请求异常");
        }
        return httpSend(httpPost, null);
    }


    /**
     * map to xml string
     *
     * @param params
     * @return
     */
    public static String mapToWechatXml(Map<String, String> params) {
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<xml>");
        for (String key : params.keySet()) {
            xmlStr.append("<").append(key).append(">").append(params.get(key)).append("</").append(key).append(">");
        }
        xmlStr.append("</xml>");

        return xmlStr.toString();
    }

    /**
     * 获取远程IP地址
     *
     * @param request 请求
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        try {
            if (Strings.isNullOrEmpty(ip) || CommonConstants.DefineSign.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (Strings.isNullOrEmpty(ip) || CommonConstants.DefineSign.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (Strings.isNullOrEmpty(ip) || CommonConstants.DefineSign.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (Strings.isNullOrEmpty(ip) || CommonConstants.DefineSign.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (Strings.isNullOrEmpty(ip) || CommonConstants.DefineSign.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception ex) {
            log.error("HttpUtils.getIpAddress 发生异常", ex);
        }
        //多级代理取第一个IP地址
        if (!Strings.isNullOrEmpty(ip) && ip.contains(CommonConstants.DefineSign.COMMA)) {
            ip = ip.split(CommonConstants.DefineSign.COMMA)[0];
        }
        return ip;
    }

    /**
     * 生成服务端下载文件名，字符编码后的文件名
     *
     * @param request  请求
     * @param fileName 文件名称
     * @return String
     */
    public static String generateDownloadName(HttpServletRequest request, String fileName) {
        final String msie = "MSIE", edge = "Edge", trident = "Trident";
        try {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains(msie) || userAgent.contains(trident) || userAgent.contains(edge)) {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            } else {
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            return fileName;
        } catch (UnsupportedEncodingException ex) {
            return fileName;
        }
    }
}
