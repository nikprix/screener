package com.mykolabs.screener.controllers;

import com.mykolabs.screener.util.JsonParser;
import com.mykolabs.screener.util.PropertiesManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.ArrayList;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends HTTP requests to retrieve required for screenshoting pages.
 *
 * @author nikprixmar
 */
public class HttpRequestController {

    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final static String DOMAIN_PROTOCOL = "http://";
    private final static String WWW = "www";

    private final String domain;
    private final String collectionId;
    private final String presentationId;

    /**
     * Initializing fields.
     *
     * @param domain
     * @param collectionId
     * @param presentationId
     */
    public HttpRequestController(String domain, String collectionId, String presentationId) {
        this.domain = domain;
        this.collectionId = collectionId;
        this.presentationId = presentationId;
    }

    /**
     * Returns ArrayList of all page ids, retrieved from manifest.
     *
     * @return
     */
    public ArrayList<String> getPagesList() {
        return JsonParser.retrievePagesArray(getProgramJson());
    }

    /**
     * Retrieves full program's JSON.
     *
     * @return
     */
    private String getProgramJson() {
        try {

            Client client = Client.create();
            // get request URL
            String resourceURL = createRequestUrl();
            log.info("Sending GET request to: " + resourceURL);

            WebResource webResource = client.resource(resourceURL);
            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            log.info("RESPONSE: " + output);
            return output;

        } catch (Exception e) {
            e.printStackTrace();
            log.info("There was an error while processing request, please try again.");
            return "{\"error\": \"There was an error while processing request, please try again.\"}";
        }
    }

    /**
     * Creates request URL.
     *
     * @return
     */
    private String createRequestUrl() {
        Properties props = PropertiesManager.getProperties();
        // Creating request URL
        return new StringBuilder()
                .append(DOMAIN_PROTOCOL)
                .append(WWW)
                .append(".")
                .append(domain)
                .append(props.getProperty("API_PATH"))
                .append(collectionId)
                .append("-")
                .append(presentationId)
                .append(props.getProperty("API_FORMAT"))
                .append("?")
                .append(props.getProperty("API_VERSION"))
                .toString();
    }

    
}
