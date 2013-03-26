/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.client.contenthub.search.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.common.SolrDocument;

/**
 * Represent a unique Document as a result of a Search in Stanbol
 * 
 * @author Rafa Haro
 *
 */
public class DocumentResult{

    /**
     * Shared Fields
     */
    final private String id;
    final private String dereferencableURI;
    final private String mimetype;
    final private long enhancementCount;
    final private String title;
    
    /**
     * Store the SolrDocument to allow users to access semantic fields easily
     */
    private SolrDocument solrDocument;
    
    DocumentResult(String id, String mimeType, long enhancementCount, String title) {
        this.id = id;
        this.mimetype = mimeType;
        this.title = (title == null || title.trim().equals("") ? id : title);
        this.enhancementCount = enhancementCount;
        this.dereferencableURI = null;
    }

    DocumentResult(String id, String mimeType, long enhancementCount, String title, SolrDocument solrDocument) {
        this.id = id;
        this.mimetype = mimeType;
        this.title = (title == null || title.trim().equals("") ? id : title);
        this.enhancementCount = enhancementCount;
        this.solrDocument = solrDocument;
        this.dereferencableURI = null;
    }

    DocumentResult(String id,
                              String dereferencableURI,
                              String mimeType,
                              long enhancementCount,
                              String title,
                              SolrDocument solrDocument) {
        this.id = id;
        this.dereferencableURI = dereferencableURI;
        this.mimetype = mimeType;
        this.title = (title == null || title.trim().equals("") ? id : title);
        this.enhancementCount = enhancementCount;
        this.solrDocument = solrDocument;
    }

    /**
     * 
     * @return Document ID
     */
    public String getLocalId() {
        return this.id;
    }

    /**
     * 
     * @return Document Dereferencable URI
     */
    public String getDereferencableURI() {
        return this.dereferencableURI;
    }

    /**
     * 
     * @return Document Mimetype
     */
    public String getMimetype() {
        return this.mimetype;
    }

    /**
     * 
     * @return Number of enhancements associated to the Document
     */
    public long getEnhancementCount() {
        return this.enhancementCount;
    }

    /**
     * 
     * @return Document Title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * 
     * @param fieldName Solr Field Name
     * @return String value of the Solr Field passed by parameter
     */
    String getFieldValue(String fieldName){
        return solrDocument.getFieldValue(fieldName).toString();
    }
    
    /**
     * 
     * @param fieldName Solr Field Name
     * @return List of String values of the Solr Field passed by parameter
     */
    Collection<String> getFieldValues(String fieldName){
        Collection<String> result = new ArrayList<String>();
        for(Object o:solrDocument.getFieldValues(fieldName))
            result.add(o.toString());
        
        return result;
        
    }
    
    /**
     * 
     * @return Solr Document associated to the Document
     */
    public SolrDocument getSolrDocument(){
        return this.solrDocument;
    }
    
    void setSolrDocument(SolrDocument solrDocument){
        this.solrDocument = solrDocument;
    }

}
