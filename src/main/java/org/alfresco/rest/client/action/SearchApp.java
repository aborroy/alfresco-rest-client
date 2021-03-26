package org.alfresco.rest.client.action;

import org.alfresco.rest.client.util.FeignClientInterceptor;
import org.alfresco.rest.client.util.User;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class SearchApp {

    static final Logger LOG = LoggerFactory.getLogger(SearchApp.class);

    @Autowired
    SearchApi searchApi;

    @Autowired
    FeignClientInterceptor interceptor;

    @Value("${action.site.sites}")
    Integer siteCount;

    @Value("${action.site.users}")
    Integer userCount;

    @Value("${action.search.count}")
    Integer searchCount;

    List<String> terms = List.of("english", "after", "petroleum", "team", "send", "standard",
            "value", "risk", "very", "real", "class", "requirements",
            "fold", "able", "reply", "version", "story", "just", "this",
            "free", "blue", "green", "gauge", "flat", "friend", "note");

    public void run(String... args) {

        Instant start = Instant.now();

        IntStream.range(0, searchCount).forEach(i -> {

            String term = terms.get(i % terms.size());
            String site = "test-" + new Random().nextInt(siteCount - 1);
            String user = "test-" + new Random().nextInt(userCount - 1);

            interceptor.setUser(new User().user(user).pass(user));

            searchApi.search(new SearchRequest()
                    .query(new RequestQuery()
                            .language(RequestQuery.LanguageEnum.AFTS)
                            .query("((SITE:\"" + site + "\" AND (cm:name:\"" + term + "\" OR cm:title:\"" + term +
                                    "\" OR cm:description:\"" + term + "\" OR " +
                                    "TEXT:\"" + term + "\" OR TAG:\"" + term + "\")))")))
                    .getBody().getList().getEntries();

            searchApi.search(new SearchRequest()
                    .query(new RequestQuery()
                            .language(RequestQuery.LanguageEnum.AFTS)
                            .query("(SITE:\"" + site + "\" AND TYPE:\"cm:content\" AND cm:name:\"*" + term + "*\")"))).getBody().getList().getEntries();

        });

        Instant end = Instant.now();

        Duration interval = Duration.between(start, end);

        System.out.println("Execution time in millis: " + interval.toMillis());


    }


}
