package org.apache.stanbol.client.model;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.stanbol.client.services.StanbolContenthubService;

public class ContentHubDocument {
    private String index = StanbolContenthubService.STANBOL_DEFAULT_INDEX;
    private String id = "";
    private String title;
    private File contentFile;
    private InputStream contentStream;
    private String contentUrl;
    private Map<String, Serializable> metadata;
    
    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public File getContentFile() {
        return contentFile;
    }
    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
    }
    public InputStream getContentStream() {
        return contentStream;
    }
    public void setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
    }
    public String getContentUrl() {
        return contentUrl;
    }
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
    public Map<String, Serializable> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, Serializable> metadata) {
        this.metadata = metadata;
    }
}
