package org.alfresco.rest.client.action;

import org.alfresco.rest.client.util.FeignClientInterceptor;
import org.alfresco.rest.client.util.User;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetRowEntry;
import org.alfresco.search.model.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class SearchApp {

    static final Logger LOG = LoggerFactory.getLogger(SearchApp.class);

    @Autowired
    SearchApi searchApi;

    @Autowired
    FeignClientInterceptor interceptor;

    @Value("${action.search.count}")
    Integer searchCount;

    public void run(String... args) {

        interceptor.setUser(new User().user("test-1").pass("test-1"));

        Instant start = Instant.now();

        IntStream.range(0, searchCount).forEach(i -> {
            List<ResultSetRowEntry> result = searchApi.search(new SearchRequest()
                    .query(new RequestQuery()
                            .language(RequestQuery.LanguageEnum.AFTS)
                            .query("((SITE:\"test-0\" AND (cm:name:\"test\" OR cm:title:\"test\" OR cm:description:\"test\" OR " +
                                    "TEXT:\"test\" OR TAG:\"test\")))"))).getBody().getList().getEntries();
            Assert.isTrue(result.size() > 0, "result count is 0");

            result = searchApi.search(new SearchRequest()
                    .query(new RequestQuery()
                            .language(RequestQuery.LanguageEnum.AFTS)
                            .query("(SITE:\"test-0\" AND TYPE:\"cm:content\" AND cm:name:\"*sample*\")"))).getBody().getList().getEntries();
            Assert.isTrue(result.size() > 0, "result count is 0");
        });

        Instant end = Instant.now();

        Duration interval = Duration.between(start, end);

        System.out.println("Execution time in millis: " + interval.toMillis());


    }


}
