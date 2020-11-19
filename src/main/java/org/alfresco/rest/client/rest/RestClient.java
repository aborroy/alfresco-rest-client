package org.alfresco.rest.client.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.alfresco.rest.client.rest.bean.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
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

    public String createUser(String name) {

        UserRequestBean userRequestBean = new UserRequestBean();
        userRequestBean.setId(name);
        userRequestBean.setFirstName(name);
        userRequestBean.setLastName(name);
        userRequestBean.setEmail(name + "@test.com");
        userRequestBean.setEnabled(true);
        userRequestBean.setPassword(name);

        try {

            String json = new ObjectMapper().writeValueAsString(userRequestBean);
            RequestBody jsonBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(baseUrl + "/people")
                    .post(jsonBody)
                    .addHeader("Authorization", authHeader)
                    .build();

            Call call = client.newCall(request);
            ResponseBody responseBody = call.execute().body();
            UserResponseBean userResponseBean = new ObjectMapper().readValue(Objects.requireNonNull(responseBody).string(), UserResponseBean.class);
            return userResponseBean.getEntry().getId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createGroup(String name, List<String> parentGroupIds) {

        GroupRequestBean groupRequestBean = new GroupRequestBean();
        groupRequestBean.setId(name);
        groupRequestBean.setDisplayName(name);
        groupRequestBean.setParentIds(parentGroupIds);

        try {

            String json = new ObjectMapper().writeValueAsString(groupRequestBean);
            RequestBody jsonBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(baseUrl + "/groups")
                    .post(jsonBody)
                    .addHeader("Authorization", authHeader)
                    .build();

            Call call = client.newCall(request);
            ResponseBody responseBody = call.execute().body();

            assert responseBody != null;
            GroupResponseBean groupResponseBean = new ObjectMapper().readValue(responseBody.string(), GroupResponseBean.class);
            return groupResponseBean.getEntry().getId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String getAuthHeader(String name, String password) {
        String auth = name + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        return "Basic " + new String(encodedAuth);
    }

    public void createSite(String name, String visibility) {

        SiteRequestBean siteRequestBean = new SiteRequestBean();
        siteRequestBean.setId(name);
        siteRequestBean.setGuid(name);
        siteRequestBean.setTitle(name);
        siteRequestBean.setDescription(name);
        siteRequestBean.setVisibility(visibility);

        try {

            String json = new ObjectMapper().writeValueAsString(siteRequestBean);
            RequestBody jsonBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(baseUrl + "/sites")
                    .post(jsonBody)
                    .addHeader("Authorization", getAuthHeader(name, name))
                    .build();

            Response response = client.newCall(request).execute();
            response.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
