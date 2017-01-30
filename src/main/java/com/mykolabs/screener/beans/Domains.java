package com.mykolabs.screener.beans;

/**
 *
 * @author nikprixmar
 */
public class Domains {

    private final String domain;
    private final String url;

    /**
     * Constructor for init.
     *
     * @param domain
     * @param url
     */
    public Domains(String domain, String url) {
        this.domain = domain;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Overrides toString() and prints domain so whenever object gets printed -
     * it will print domain name.
     *
     * @return
     */
    @Override
    public String toString() {
        return domain;
    }
}
