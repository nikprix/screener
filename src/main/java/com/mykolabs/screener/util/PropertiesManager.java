package com.mykolabs.screener.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages project's properties.
 *
 * @author nikprixmar
 */
public class PropertiesManager {

    /**
     * Returns properties.
     *
     * @return
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        InputStream in = PropertiesManager.class.getClassLoader().getResourceAsStream("project.properties");

        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(PropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
}
