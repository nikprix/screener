package com.mykolabs.screener.beans;

/**
 *
 * @author nikprixmar
 */
public class Domains {

    private final String domain;
    private final String url;
    private final String path;

    /**
     * Constructor for init.
     *
     * @param domain
     * @param url
     * @param path
     */
    public Domains(String domain, String url, String path) {
        this.domain = domain;
        this.url = url;
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
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
