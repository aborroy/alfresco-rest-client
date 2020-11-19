package org.alfresco.rest.client.action;

import org.alfresco.rest.client.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Component
public class ActionCreateGroup {

    static final Logger LOG = LoggerFactory.getLogger(ActionCreateGroup.class);

    @Autowired
    RestClient restClient;

    public void execute(int levelCount, int groupCount) {

        AtomicReference<List<String>> parentGroups = new AtomicReference<>(Collections.emptyList());
        IntStream.rangeClosed(1, levelCount).forEach(l -> {
            List<String> groupsCreated = new ArrayList<>();
            IntStream.rangeClosed(1, groupCount).parallel().forEach(n -> {
                groupsCreated.add(restClient.createGroup("group-l"+ l + "-" + n, parentGroups.get()));
                LOG.info(String.format("created document %s", "group-l"+ l + "-" + n));
            });
            parentGroups.set(groupsCreated);
        });

    }

}
