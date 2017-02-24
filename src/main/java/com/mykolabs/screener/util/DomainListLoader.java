package com.mykolabs.screener.util;

import com.mykolabs.screener.beans.Domains;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads domains list with respective URLs from .csv file and
 * supplies it into ArrayList of Domains objects. Further, this ArrayList will
 * be used to populate domainChoiceList
 *
 * @author nikprixmar
 */
public class DomainListLoader {

    private static final String SCV_FILE = "domains.csv";

    //create an object of DomainListLoader
    private static DomainListLoader instance = new DomainListLoader();

    // using singleton pattern
    private DomainListLoader() {
    }

    //Get the only object available
    public static DomainListLoader getInstance() {
        return instance;
    }

    /**
     * Retrieves domains from the domains.csv file
     *
     * @return
     */
    public List<Domains> getDomains() {

        // domains.csv file is simple .csv located in properties
        // to fetch Domain-Url data
        List<Domains> domains = new ArrayList<>();
        CSVReader reader = null;

        try {
            // proper way to get resources in compiled JAR file: lookup in the classpath, not on the disk
            //
            reader = new CSVReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(SCV_FILE)));
            String[] line;
            // iterate through all lines of the domains.csv file
            while ((line = reader.readNext()) != null) {

                domains.add(new Domains(line[0], line[1], line[2]));

            }
            return domains;
        } catch (IOException e) {
            e.printStackTrace();
            return domains;
        }
    }
}
