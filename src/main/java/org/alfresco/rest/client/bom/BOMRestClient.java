package org.alfresco.rest.client.bom;

import okhttp3.*;
import org.alfresco.rest.client.util.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Bulk Object Mapper module client
 * Requires Json File produced with sizing-guide-generator program
 * Content files should be available in S3 Bucket inside a "contentstore" folder
 */
@Service
public class BOMRestClient {

    static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Value("${content.service.url}/alfresco/s/bulkobj/mapobjects")
    String baseUrl;

    OkHttpClient client;

    @PostConstruct
    public void init() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Create every document described in JsonFile from a folderId with specified user
     */
    public String createDocuments(User user, String folderId, File jsonFile) {

        String auth = user.getUser() + ":" + user.getPass();
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        RequestBody jsonBody = RequestBody.create(JSON, jsonFile);
        Request request = new Request.Builder()
                .url(baseUrl + "/" + folderId + "?autoCreate=y")
                .post(jsonBody)
                .addHeader("Authorization", authHeader)
                .build();
        Call call = client.newCall(request);
        try {
            return Objects.requireNonNull(call.execute().body()).string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
