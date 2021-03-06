package com.mykolabs.screener.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author nikprix
 */
public class ProgramData {

    private StringProperty domainURL;
    private StringProperty collectionId;
    private StringProperty presentationId;

    private StringProperty singlePageId;
    private List<String> programPagesIds;
    
    private String domainPath;

    /**
     * Default constructor.
     */
    public ProgramData() {
        this("", "", "", "", "", new ArrayList<>());
    }

    /**
     * Non - Default constructor.
     *
     * @param domainPath
     * @param domainURL
     * @param collectionId
     * @param presentationId
     * @param singlePageId
     * @param programPagesIds
     */
    public ProgramData(String domainPath, String domainURL, String collectionId, String presentationId, String singlePageId, ArrayList<String> programPagesIds) {
        this.domainPath = domainPath;
        this.domainURL = new SimpleStringProperty(domainURL);
        this.collectionId = new SimpleStringProperty(collectionId);
        this.presentationId = new SimpleStringProperty(presentationId);
        this.singlePageId = new SimpleStringProperty(singlePageId);
        this.programPagesIds = programPagesIds;
    }

    @Override
    public String toString() {
        return "ProgramData{" + "domainURL=" + domainURL + ", collectionId=" + collectionId + ", presentationId=" + presentationId + ", singlePageId=" + singlePageId + ", programPagesIds=" + programPagesIds + ", domainPath=" + domainPath + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.domainURL);
        hash = 79 * hash + Objects.hashCode(this.collectionId);
        hash = 79 * hash + Objects.hashCode(this.presentationId);
        hash = 79 * hash + Objects.hashCode(this.singlePageId);
        hash = 79 * hash + Objects.hashCode(this.programPagesIds);
        hash = 79 * hash + Objects.hashCode(this.domainPath);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProgramData other = (ProgramData) obj;
        if (!Objects.equals(this.domainPath, other.domainPath)) {
            return false;
        }
        if (!Objects.equals(this.domainURL, other.domainURL)) {
            return false;
        }
        if (!Objects.equals(this.collectionId, other.collectionId)) {
            return false;
        }
        if (!Objects.equals(this.presentationId, other.presentationId)) {
            return false;
        }
        if (!Objects.equals(this.singlePageId, other.singlePageId)) {
            return false;
        }
        if (!Objects.equals(this.programPagesIds, other.programPagesIds)) {
            return false;
        }
        return true;
    }

    /*
     Getters and Setters
     */

    public String getDomainPath() {
        return domainPath;
    }

    public void setDomainPath(String domainPath) {
        this.domainPath = domainPath;
    }
    
    public String getDomainURL() {
        return domainURL.get();
    }

    public void setDomainURL(final String domainUrl) {
        this.domainURL.set(domainUrl);
    }

    public StringProperty domainURL() {
        return domainURL;
    }

    public String getCollectionId() {
        return collectionId.get();
    }

    public void setCollectionId(final String collectionId) {
        this.collectionId.set(collectionId);
    }

    public StringProperty collectionId() {
        return collectionId;
    }

    public String getPresentationId() {
        return presentationId.get();
    }

    public void setPresentationId(final String presentationId) {
        this.presentationId.set(presentationId);
    }

    public StringProperty presentationId() {
        return presentationId;
    }

    public String getSinglePageId() {
        return singlePageId.get();
    }

    public void setSinglePageId(final String singlePageId) {
        this.singlePageId.set(singlePageId);
    }

    public StringProperty singlePageId() {
        return singlePageId;
    }

    public List<String> getProgramPagesIds() {
        return programPagesIds;
    }

    public void setProgramPagesIds(List<String> programPagesIds) {
        this.programPagesIds = programPagesIds;
    }

}
