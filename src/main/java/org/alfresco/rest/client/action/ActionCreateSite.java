package org.alfresco.rest.client.action;

import org.alfresco.rest.client.cmis.CmisClient;
import org.alfresco.rest.client.rest.RestClient;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Component
public class ActionCreateSite {

    static final Logger LOG = LoggerFactory.getLogger(ActionCreateSite.class);

    @Autowired
    RestClient restClient;

    @Autowired
    CmisClient cmisClient;

    public void execute(Integer siteCount, String siteVisibility) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        IntStream.rangeClosed(1, siteCount).parallel().forEach(n -> {

            String name = "site-" + n;

            String user = restClient.createUser(name);
            assert(name.equals(user));

            restClient.createSite(name, siteVisibility);
            Folder documentLibrary = cmisClient.getFolderByPath("/Sites/" + name + "/documentLibrary");
            cmisClient.createSimpleDocument(documentLibrary, name + ".txt");

            count.set(count.get() + 1);

            LOG.info(String.format("(%s/%s) created site-%s", count.get(), siteCount, name));

        });
    }

}