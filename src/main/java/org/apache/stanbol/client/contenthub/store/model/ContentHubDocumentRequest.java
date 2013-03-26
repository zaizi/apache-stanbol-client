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
package org.apache.stanbol.client.contenthub.store.model;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * This class representes a Document ready to be sent to Stanbol ContentHub to be stored as a ContentItem
 * An instance of this class contains all the information needed to perform a ContentHub store service request
 * 
 * @author Rafa Haro
 * 
 */
public class ContentHubDocumentRequest
{
    /*
     * Document's Title
     */
    private String title = "";
    
    /*
     * Document's URI
     */
    private String URI = "";
    
    /*
     * Document's File
     */
    private File contentFile = null;
    
    /*
     * Document's Input Stream
     */
    private InputStream contentStream = null;
    
    /*
     * Document's metadata
     */
    private List<Metadata> metadata = null;
    
    /*
     * Document's associated Enhancement Graph
     */
    private Model enhancementGraph = null;

    /**
     * Getter for the Document Title
     * 
     * @return Document's Title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Setter for the Document Title
     * 
     * @param title Document's Title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Getter for the Document File
     * 
     * @return Document's File
     */
    public File getContentFile()
    {
        return contentFile;
    }

    /**
     * Setter for the Document File
     * 
     * @param contentFile Document's File
     */
    public void setContentFile(File contentFile)
    {
        this.contentFile = contentFile;
    }

    /**
     * Getter for the Document Input Stream
     * 
     * @return Document's InputStream
     */
    public InputStream getContentStream()
    {
        return contentStream;
    }

    /**
     * Setter for the Document InputStream
     * 
     * @param contentStream Document's InputStream
     */
    public void setContentStream(InputStream contentStream)
    {
        this.contentStream = contentStream;
    }

    /**
     * Getter for the Document Metadata
     * 
     * @return Document's Metadata
     */
    public List<Metadata> getMetadata()
    {
        return metadata;
    }

    /**
     * Setter for the Document Metadata
     * 
     * @param metadata Document's Metadata
     */
    public void setMetadata(List<Metadata> metadata)
    {
        this.metadata = metadata;
    }

    /**
     * Getter for the Document Enhancement Graph
     * 
     * @return Document's Enhancement Graph
     */
    public Model getEnhancementGraph()
    {
        return enhancementGraph;
    }

    /**
     * Setter for the Document Enhancement Graph
     * 
     * @param enhancementGraph Document's Enhancement Graph
     */
    public void setEnhancementGraph(Model enhancementGraph)
    {
        this.enhancementGraph = enhancementGraph;
    }

    /**
     * Getter for the Document URI
     * 
     * @return Document's URI
     */
    public String getURI()
    {
        return URI;
    }

    /**
     * Setter for the Document URI
     * 
     * @param URI Document's URI
     */
    public void setURI(String URI)
    {
        this.URI = URI;
    }
}
