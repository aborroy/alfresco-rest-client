package org.alfresco.rest.client;

import org.alfresco.rest.client.action.CreateSiteApp;
import org.alfresco.rest.client.action.SearchApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

    static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Value("${action.name}")
    String action;

    @Autowired
    CreateSiteApp createSiteApp;

    @Autowired
    SearchApp searchApp;

    public static void main(String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        switch (action) {
            case "create-site":
                createSiteApp.run();
                break;
            case "search":
                searchApp.run();
                break;
            default:
                LOG.error("Action {} is not available", action);
                break;
        }

    }
}
