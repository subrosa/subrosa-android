package com.subrosagames.subrosa.mobile.android.remote;

import org.springframework.web.client.RestTemplate;

/**
 *
 */
public class ApiRequest {

    private RestTemplate restTemplate = new RestTemplate(true);

    public <T> T get(String url, Class<T> clazz) {
        return restTemplate.getForObject(url, clazz);
    }
}
