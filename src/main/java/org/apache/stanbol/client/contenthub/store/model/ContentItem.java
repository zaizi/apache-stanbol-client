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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.stanbol.client.enhancer.model.Enhancement;
import org.apache.stanbol.client.enhancer.model.EnhancementParser;
import org.apache.stanbol.client.enhancer.model.EnhancementResult;
import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class represent a Content Item retrieved from any index of Stanbol's ContentHub component
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class ContentItem
{

    /**
     * ContentItem's URI
     */
    private String URI = null;
    
    /**
     * ContentItem's Text Content
     */
    private InputStream rawContent = null;

    /**
     * ContentItem's EnhancementGraph Jena Model
     */
    private Model model;
    
    /**
     * EnhancementResult Object
     */
    private EnhancementResult enhancementResult;
    
    /**
     * ContenItem's enhancements
     */
    private Map<String, Enhancement> enhancements;
    
    /**
     * ContentItems's Metadata
     */
    private Map<String, Metadata> metadata;

    /**
     * Constructor
     * 
     * @param model RDF (Jena) Model containing ContentItem's enhancements and Metadata
     */
    public ContentItem(Model model)
    {        
        this.model = model;
        if(model.listResourcesWithProperty(RDF.type, FISE.TEXT_ANNOTATION).hasNext())
            this.URI = model.listResourcesWithProperty(RDF.type, FISE.TEXT_ANNOTATION).next().getPropertyResourceValue(FISE.EXTRACTED_FROM).getURI();
        else if(model.listResourcesWithProperty(RDF.type, FISE.USER_ANNOTATION).hasNext())
            this.URI = model.listResourcesWithProperty(RDF.type, FISE.USER_ANNOTATION).next().getPropertyResourceValue(FISE.EXTRACTED_FROM).getURI();
        else
            this.URI = null;

        this.enhancementResult = new EnhancementResult(model);
        
        enhancements = new HashMap<String, Enhancement>();
        for (Enhancement enhancement : enhancementResult.getEnhancements())
            enhancements.put(enhancement.getUri(), enhancement);

        metadata = new HashMap<String, Metadata>();
        for (Metadata metadataEx : EnhancementParser.parseMetadata(model))
            metadata.put(metadataEx.name, metadataEx);

    }

    /**
     * Constructor
     * 
     * @param model RDF (Jena) Model containing ContentItem's enhancements and Metadata
     * @param rawContent InputStream with the Text Content of the Document
     */
    public ContentItem(Model model, InputStream rawContent)
    {
        this(model);
        this.rawContent = rawContent;

    }

    /**
     * Get the content id
     * 
     * @return Content id
     */
    public String getURI()
    {
        return URI;
    }

    /**
     * Get the content enhancements
     * 
     * @return Content enhancements
     */
    public Map<String, Enhancement> getEnhancements()
    {
        return enhancements;
    }

    /**
     * Get ContentItem's Enhancement by Enhancement's URI
     * 
     * @param URI Enhancement's URI
     * @return Enhancement identified by its URI
     */
    public Enhancement getEnhancement(String URI)
    {
        return enhancements.get(URI);
    }

    /**
     * Return the number of the enhancements for this ContentItem
     * 
     * @return Number of ContentItem's Enhancements
     */
    public int getEnhancementCount()
    {
        return enhancements.size();
    }

    /**
     * Get ContentItem's RAW Content
     * 
     * @return InputStream with the Document Text Content
     */
    public InputStream getRawContent()
    {
        return rawContent;
    }

    /**
     * Get ContentItem's Metadata
     * 
     * @return ContentItem's Metadata
     */
    public Map<String, Metadata> getMetadata()
    {
        return metadata;
    }

    /**
     * Get ContentItem's Metadata by Name
     * 
     * @param name Metadata's Name
     * @return ContentItem's Metadata identified by the passed name
     */
    public Metadata getMetadata(String name)
    {
        return metadata.get(name);
    }
    
    /**
     * Get Enhancement Graph
     * 
     * @return Jena {@link Model} containing the Enhancement Graph
     */
    public Model getEnhancementGraph()
    {
        return model;
    }
    
    /**
     * Get Enhancement Result Object 
     * 
     * @return {@link EnhancementResult}
     */
    public EnhancementResult getEnhancementResult(){
        return enhancementResult;
    }

}
