package org.alfresco.rest.client.action;

import org.alfresco.rest.client.cmis.CmisClient;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class ActionCreateDocument {

    static final Logger LOG = LoggerFactory.getLogger(ActionCreateDocument.class);

    @Autowired
    CmisClient cmisClient;

    public void execute(Folder rootFolder, Integer documentCount, Integer splitDocumentsInFolders) {

        Map<Integer, Folder> folders = new HashMap<>();
        IntStream.rangeClosed(1, documentCount / splitDocumentsInFolders).parallel().forEach(n -> {
            folders.computeIfAbsent(n, x -> cmisClient.createFolder(rootFolder, "folder-" + n));
            LOG.info(String.format("created folder %s", folders.get(n).getId()));
        });

        IntStream.rangeClosed(1, documentCount).parallel().forEach(n -> {
            Folder folder = folders.get(n / splitDocumentsInFolders);
            final Document doc = cmisClient.createSimpleDocument(folder, "text-" + n + ".txt", "txt");
            LOG.info(String.format("created document %s", doc.getId()));
        });

    }

}
