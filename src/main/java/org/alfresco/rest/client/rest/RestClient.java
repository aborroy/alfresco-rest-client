package org.alfresco.rest.client.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.alfresco.rest.client.rest.bean.GroupRequestBean;
import org.alfresco.rest.client.rest.bean.GroupResponseBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RestClient {

    static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Value("${alfresco.repository.user}")
    String alfrescoUser;
    @Value("${alfresco.repository.pass}")
    String alfrescoPass;
    @Value("${alfresco.repository.url}/api/-default-/public/alfresco/versions/1")
    String baseUrl;

    OkHttpClient client;
    String authHeader;

    @PostConstruct
    public void init() {

        client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String auth = alfrescoUser + ":" + alfrescoPass;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        authHeader = "Basic " + new String(encodedAuth);

    }

    public String createGroup(String name, List<String> parentGroupIds)
    {

        ObjectMapper objectMapper = new ObjectMapper();

        GroupRequestBean groupRequestBean = new GroupRequestBean();
        groupRequestBean.setId(name);
        groupRequestBean.setDisplayName(name);
        groupRequestBean.setParentIds(parentGroupIds);

        try {
            String json = objectMapper.writeValueAsString(groupRequestBean);
            RequestBody jsonBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(baseUrl + "/groups")
                    .post(jsonBody)
                    .addHeader("Authorization", authHeader)
                    .build();

            Call call = client.newCall(request);
            ResponseBody responseBody = call.execute().body();

            assert responseBody != null;
            GroupResponseBean groupResponseBean = objectMapper.readValue(responseBody.string(), GroupResponseBean.class);
            return groupResponseBean.getEntry().getId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
