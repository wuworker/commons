package com.wxl.commons.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.UnsupportedCharsetException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wuxingle on 2024/07/26.
 * 简易http工具
 */
public class HttpUtils {

    private static final String DEFAULT_CHARSET = "utf-8";

    private static Gson gson = new GsonBuilder().create();

    private static HttpClient client = HttpClient.newHttpClient();

    /**
     * 把参数转为key=value,用&分隔的形式
     */
    public static <K, V> String toQuery(Map<K, V> query, String encode) {
        if (MapUtils.isEmpty(query)) {
            return "";
        }
        StringBuilder queryBuilder = new StringBuilder();
        try {
            for (Iterator<Map.Entry<K, V>> it = query.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<K, V> entry = it.next();
                K key = entry.getKey();
                V value = entry.getValue();
                queryBuilder.append(key == null ? "" : URLEncoder.encode(key.toString(), encode))
                        .append("=")
                        .append(value == null ? "" : URLEncoder.encode(value.toString(), encode));
                if (it.hasNext()) {
                    queryBuilder.append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(encode);
        }
        return queryBuilder.toString();
    }

    public static <K, V> String toQuery(Map<K, V> query) {
        return toQuery(query, DEFAULT_CHARSET);
    }

    /**
     * 从key=value,用&分隔的形式解析为map
     */
    public static Map<String, String> fromQuery(String query, String encode) {
        if (StringUtils.isBlank(query)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new LinkedHashMap<>();
        try {
            for (String kvs : query.split("&")) {
                String[] kv = kvs.split("=");
                if (kv.length == 0) {
                    continue;
                }
                map.put(URLDecoder.decode(kv[0], encode),
                        kv.length > 1 ? URLDecoder.decode(kv[1], encode) : null);
            }
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(encode);
        }
        return map;
    }

    public static Map<String, String> fromQuery(String query) {
        return fromQuery(query, DEFAULT_CHARSET);
    }

    /**
     * 从地址中获取请求参数
     */
    public static String getQueryFromURI(String uri) {
        try {
            return new URI(uri).getQuery();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri is illegal:" + uri);
        }
    }

    /**
     * 构建url
     */
    public static <K, V> String buildURL(String url, Map<K, V> params, String encode) {
        String query = toQuery(params, encode);
        if (StringUtils.isBlank(query)) {
            return url;
        }
        int refIndex;
        String ref = "", queryUrl = url;
        if ((refIndex = url.indexOf("#")) >= 0) {
            ref = url.substring(refIndex);
            queryUrl = url.substring(0, refIndex);
        }
        return queryUrl + (url.indexOf("?") > 0 ? "&" : "?") + query + ref;
    }

    public static <K, V> String buildURL(String url, Map<K, V> params) {
        return buildURL(url, params, DEFAULT_CHARSET);
    }

    /**
     * get请求
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(buildURL(url, params)))
                .GET();
        if (timeout != null) {
            builder.timeout(timeout);
        }
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(builder::header);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String doGet(String url, Map<String, String> params, Duration timeout) throws IOException, InterruptedException {
        return doGet(url, params, null, timeout);
    }

    public String doGet(String url, Map<String, String> params) throws IOException, InterruptedException {
        return doGet(url, params, null, null);
    }

    public String doGet(String url) throws IOException, InterruptedException {
        return doGet(url, null, null, null);
    }

}
