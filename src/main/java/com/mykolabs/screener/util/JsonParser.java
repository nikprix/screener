package com.mykolabs.screener.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple JSON parser class using Jackson mapping.
 *
 * @author nikprixmar
 */
public class JsonParser {

    public static ArrayList<String> retrievePagesArray(String json) {

        ArrayList<String> programPages = new ArrayList<>();

        try {
            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //read JSON like DOM Parser
            JsonNode rootNode = objectMapper.readTree(json);

            // retrive "response" object
            JsonNode responseNode = rootNode.path("response");

            // retrieve "content" object
            JsonNode contentNode = responseNode.path("content");

            // retrieve "manifest" Array
            JsonNode manifestNode = contentNode.path("manifest");
            Iterator<JsonNode> manifestElements = manifestNode.elements();

            // looping through the contents of the Array,
            // however, there should be only one object with current implementation
            while (manifestElements.hasNext()) {
                JsonNode manifestObjects = manifestElements.next();

                // retrieve "pages" object
                JsonNode pagesNode = manifestObjects.path("pages");
                Iterator<JsonNode> pagesElements = pagesNode.elements();

                // retrieveing ALL pages from "pages" array
                while (pagesElements.hasNext()) {
                    JsonNode page = pagesElements.next();

                    // add page to the ArrayList
                    programPages.add(page.asText());
                }
            }
            return programPages;

        } catch (IOException ex) {
            ex.printStackTrace();
            return programPages;
        }
    }

}
