package org.alfresco.rest.client.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUser(User user) {
        userThreadLocal.set(user);
    }

    public User getUser() {
        return userThreadLocal.get();
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.removeHeader(AUTHORIZATION_HEADER);
        requestTemplate.header(AUTHORIZATION_HEADER,
                "Basic " + Base64Utils.encodeToString(
                        (userThreadLocal.get().user + ":" + userThreadLocal.get().pass).getBytes(Util.ISO_8859_1)));
    }
}
