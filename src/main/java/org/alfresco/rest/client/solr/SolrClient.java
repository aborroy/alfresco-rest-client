package org.alfresco.rest.client.solr;

import org.springframework.beans.factory.annotation.Value;

public class SolrClient {

    @Value("${alfresco.solr.url}")
    String solrUrl;

    @Value("${alfresco.solr.url}/alfresco")
    String baseUrl;

}
