package org.alfresco.rest.client.action;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.PeopleApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.handler.TagsApi;
import org.alfresco.core.model.*;
import org.alfresco.rest.client.bom.BOMRestClient;
import org.alfresco.rest.client.util.FeignClientInterceptor;
import org.alfresco.rest.client.util.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
public class CreateSiteApp {

    static final Logger LOG = LoggerFactory.getLogger(CreateSiteApp.class);

    @Value("${action.site.sites}")
    Integer siteCount;

    @Value("${action.site.users}")
    Integer userCount;

    @Value("${action.site.json.path}")
    String jsonPath;

    @Autowired
    SitesApi sitesApi;
    @Autowired
    NodesApi nodesApi;
    @Autowired
    PeopleApi peopleApi;
    @Autowired
    TagsApi tagsApi;

    @Autowired
    FeignClientInterceptor interceptor;
    @Autowired
    BOMRestClient bomRestClient;

    /**
     * Create siteCount sites and userCount users.
     * Distribute all the documents available in jsonPath among all the sites
     * with an uniform distribution for user creation.
     */
    public void run() throws IOException {

        interceptor.setUser(new User().user("admin").pass("admin"));

        final List<Path> jsonFiles = Files.list(Paths.get(jsonPath)).collect(toList());

        // Split available jsonFiles into sites
        int splitS = jsonFiles.size() / siteCount;
        final AtomicInteger counterSite = new AtomicInteger();
        final List<List<Path>> jsonFilesBySite = new ArrayList<>(jsonFiles.stream()
                .collect(Collectors.groupingBy(it -> counterSite.getAndIncrement() / splitS))
                .values());

        IntStream.range(0, siteCount).forEach(siteNumber -> {

            Site site = Objects.requireNonNull(sitesApi.createSite(
                    new SiteBodyCreate()
                            .id("test-" + siteNumber)
                            .title("test-" + siteNumber)
                            .visibility(SiteBodyCreate.VisibilityEnum.MODERATED),
                    null, null, null).getBody()).getEntry();
            LOG.info("Site {} created", "test-" + siteNumber);

            SiteContainer siteContainer = Objects.requireNonNull(sitesApi.getSiteContainer(site.getId(), "documentLibrary", null).getBody()).getEntry();
            tagsApi.createTagForNode(siteContainer.getId(), new TagBody().tag("test"), null);
            LOG.info("Site folder is {}", siteContainer.getId());

            // Split available jsonFiles into users
            int splitU = jsonFilesBySite.get(siteNumber).size() / userCount;
            final AtomicInteger counterUser = new AtomicInteger();
            final List<List<Path>> jsonFilesByUser = new ArrayList<>(jsonFilesBySite.get(siteNumber).stream()
                    .collect(Collectors.groupingBy(it -> counterUser.getAndIncrement() / splitU))
                    .values());

            IntStream.range(0, userCount).forEach(userNumber -> {

                interceptor.setUser(new User().user("admin").pass("admin"));

                if (Objects.requireNonNull(peopleApi.getPerson("test-" + userNumber, null).getBody()).getEntry() == null) {
                    Objects.requireNonNull(peopleApi.createPerson(new PersonBodyCreate()
                            .id("test-" + userNumber)
                            .password("test-" + userNumber)
                            .enabled(true)
                            .emailNotificationsEnabled(true)
                            .email("test-" + userNumber + "@email.fake")
                            .firstName("test-" + userNumber)
                            .lastName("test-" + userNumber), null).getBody()).getEntry();
                }

                if (Objects.requireNonNull(sitesApi.getSiteMembershipForPerson("test-" + userNumber, "test-" + siteNumber).getBody()).getEntry() == null) {
                    sitesApi.createSiteMembership("test-" + siteNumber,
                            new SiteMembershipBodyCreate()
                                    .id("test-" + userNumber)
                                    .role(SiteMembershipBodyCreate.RoleEnum.SITEMANAGER),
                            null);
                }

                interceptor.setUser(new User().user("test-" + userNumber).pass("test-" + userNumber));
                LOG.info("User {} created", "test-" + userNumber);

                try {
                    createDocuments(siteContainer, jsonFilesByUser.get(userNumber));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

        });

    }

    private void createDocuments(SiteContainer siteContainer, List<Path> jsonFiles) throws IOException {

        Node parentFolder = Objects.requireNonNull(nodesApi.createNode(siteContainer.getId(),
                new NodeBodyCreate()
                        .nodeType("cm:folder")
                        .name("Folder-" + System.currentTimeMillis()),
                null, null, null, null, null).getBody()).getEntry();

        // Create files and metadata requests
        LOG.info("Processing {} JSON files...", jsonFiles.size());
        User user = interceptor.getUser();
        final AtomicInteger counterFiles = new AtomicInteger(1);
        jsonFiles.stream().parallel().forEach(json -> {
            String response = bomRestClient.createDocuments(user, parentFolder.getId(), json.toFile());
            LOG.info("Processed {} files with response {} [{}]", counterFiles.getAndIncrement(), response, json.getFileName());
        });
        LOG.info("... all JSON files processed!");

    }

}
