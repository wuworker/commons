package com.wxl.commons.util.net;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Created by wuxingle on 2024/07/26.
 * http测试
 */
public class HttpUtilsTest {

    @Test
    public void test1() throws Exception{
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.baidu.com"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.headers());
        System.out.println(response.body());
    }
}
