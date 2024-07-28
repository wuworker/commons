package com.wxl.commons.util.net;


import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by wuxingle on 2024/07/26.
 * 简易http工具
 */
public class HttpUtils {

    private static final String DEFAULT_CHARSET = "utf-8";

    private static final Gson gson = new GsonBuilder().create();

    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpClient getDefaultClient() {
        return client;
    }

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
                queryBuilder.append(key == null ? "" : URLEncoder.encode(key.toString(), encode)).append("=").append(value == null ? "" : URLEncoder.encode(value.toString(), encode));
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
                map.put(URLDecoder.decode(kv[0], encode), kv.length > 1 ? URLDecoder.decode(kv[1], encode) : null);
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
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(buildURL(url, params))).GET();
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

    public static String doGet(String url, Map<String, String> params, Duration timeout) throws IOException, InterruptedException {
        return doGet(url, params, null, timeout);
    }

    public static String doGet(String url, Map<String, String> params) throws IOException, InterruptedException {
        return doGet(url, params, null, (Duration) null);
    }

    public static String doGet(String url) throws IOException, InterruptedException {
        return doGet(url, null, null, (Duration) null);
    }

    public static <T> T doGet(String url, Map<String, String> params, Map<String, String> headers, Duration timeout, Type type) throws IOException, InterruptedException {
        String result = doGet(url, params, headers, timeout);
        return gson.fromJson(result, type);
    }

    public static <T> T doGet(String url, Map<String, String> params, Duration timeout, Type type) throws IOException, InterruptedException {
        return doGet(url, params, null, timeout, type);
    }

    public static <T> T doGet(String url, Map<String, String> params, Type type) throws IOException, InterruptedException {
        return doGet(url, params, null, null, type);
    }

    public static <T> T doGet(String url, Type type) throws IOException, InterruptedException {
        return doGet(url, null, null, null, type);
    }

    /**
     * post请求
     */
    public static String doPost(String url, HttpRequest.BodyPublisher publisher, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        if (publisher == null) {
            publisher = HttpRequest.BodyPublishers.noBody();
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).POST(publisher);
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

    public static String doPost(String url, HttpRequest.BodyPublisher publisher, Duration timeout) throws IOException, InterruptedException {
        return doPost(url, publisher, null, timeout);
    }

    public static String doPost(String url, HttpRequest.BodyPublisher publisher) throws IOException, InterruptedException {
        return doPost(url, publisher, null, (Duration) null);
    }

    public static String doPost(String url) throws IOException, InterruptedException {
        return doPost(url, null, null, (Duration) null);
    }

    public static <T> T doPost(String url, HttpRequest.BodyPublisher publisher, Map<String, String> headers, Duration timeout, Type type) throws IOException, InterruptedException {
        String result = doPost(url, publisher, headers, timeout);
        return gson.fromJson(result, type);
    }

    public static <T> T doPost(String url, HttpRequest.BodyPublisher publisher, Duration timeout, Type type) throws IOException, InterruptedException {
        return doPost(url, publisher, null, timeout, type);
    }

    public static <T> T doPost(String url, HttpRequest.BodyPublisher publisher, Type type) throws IOException, InterruptedException {
        return doPost(url, publisher, null, null, type);
    }

    public static <T> T doPost(String url, Type type) throws IOException, InterruptedException {
        return doPost(url, null, null, null, type);
    }

    /**
     * post form请求
     */
    public static <K, V> String doFormPost(String url, Map<K, V> form, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher publisher;
        if (MapUtils.isEmpty(form)) {
            publisher = HttpRequest.BodyPublishers.noBody();
        } else {
            publisher = HttpRequest.BodyPublishers.ofString(toQuery(form));
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).header(HttpHeaders.CONTENT_TYPE, MediaType.FORM_DATA.withCharset(StandardCharsets.UTF_8).toString()).POST(publisher);
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

    public static <K, V> String doFormPost(String url, Map<K, V> form, Duration timeout) throws IOException, InterruptedException {
        return doFormPost(url, form, null, timeout);
    }

    public static <K, V> String doFormPost(String url, Map<K, V> form) throws IOException, InterruptedException {
        return doFormPost(url, form, null, (Duration) null);
    }

    public static <T, K, V> T doFormPost(String url, Map<K, V> form, Map<String, String> headers, Duration timeout, Type type) throws IOException, InterruptedException {
        String result = doFormPost(url, form, headers, timeout);
        return gson.fromJson(result, type);
    }

    public static <T, K, V> T doFormPost(String url, Map<K, V> form, Duration timeout, Type type) throws IOException, InterruptedException {
        return doFormPost(url, form, null, timeout, type);
    }

    public static <T, K, V> T doFormPost(String url, Map<K, V> form, Type type) throws IOException, InterruptedException {
        return doFormPost(url, form, null, null, type);
    }

    /**
     * post json请求
     */
    public static String doJsonPost(String url, Object data, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher publisher;
        if (data == null) {
            publisher = HttpRequest.BodyPublishers.noBody();
        } else {
            publisher = HttpRequest.BodyPublishers.ofString(gson.toJson(data));
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString()).POST(publisher);
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

    public static String doJsonPost(String url, Object data, Duration timeout) throws IOException, InterruptedException {
        return doJsonPost(url, data, null, timeout);
    }

    public static String doJsonPost(String url, Object data) throws IOException, InterruptedException {
        return doJsonPost(url, data, null, (Duration) null);
    }

    public static <T> T doJsonPost(String url, Object data, Map<String, String> headers, Duration timeout, Type type) throws IOException, InterruptedException {
        String result = doJsonPost(url, data, headers, timeout);
        return gson.fromJson(result, type);
    }

    public static <T> T doJsonPost(String url, Object data, Duration timeout, Type type) throws IOException, InterruptedException {
        return doJsonPost(url, data, null, timeout, type);
    }

    public static <T> T doJsonPost(String url, Object data, Type type) throws IOException, InterruptedException {
        return doJsonPost(url, data, null, null, type);
    }

    /**
     * 其他请求
     */
    public static String execute(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static <T> T execute(HttpRequest request, Type type) throws IOException, InterruptedException {
        String result = execute(request);
        return gson.fromJson(result, type);
    }
}
