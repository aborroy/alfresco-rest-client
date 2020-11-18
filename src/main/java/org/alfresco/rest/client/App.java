package org.alfresco.rest.client;

import org.alfresco.rest.client.action.ActionCreateDocument;
import org.alfresco.rest.client.action.ActionCreateGroup;
import org.alfresco.rest.client.cmis.CmisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication
public class App implements CommandLineRunner {

    static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    Environment env;

    @Autowired
    CmisClient cmisClient;

    @Autowired
    ActionCreateDocument actionCreateDocument;

    @Autowired
    ActionCreateGroup actionCreateGroup;

    public void run(String... args) {

        switch (Objects.requireNonNull(env.getProperty("action"))) {
            case "document":
                LOG.info("Creating documents...");
                actionCreateDocument.execute(
                        cmisClient.getRootFolder(),
                        Integer.parseInt(env.getProperty("document.count", "100")),
                        Integer.parseInt(env.getProperty("document.folder.split.count", "10")));
                LOG.info("...documents created");
                break;
            case "group":
                LOG.info("Creating groups...");
                actionCreateGroup.execute(
                        Integer.parseInt(env.getProperty("group.levels", "2")),
                        Integer.parseInt(env.getProperty("group.count", "10")));
                LOG.info("...groups created");
                break;
            default:
                LOG.error("Action {} is not supported", env.getProperty("action"));
                break;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
