package com.wxl.commons.http;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Create by wuxingle on 2020/11/20
 * rest template测试
 */
public class RestTemplateTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testGet() {
        String html = restTemplate.getForObject("https://www.baidu.com", String.class);
        System.out.println(html);
    }

}
