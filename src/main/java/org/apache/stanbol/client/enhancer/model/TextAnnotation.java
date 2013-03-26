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
package org.apache.stanbol.client.enhancer.model;

import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents a text annotation from FISE ontology
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class TextAnnotation extends Annotation
{

    // properties
    private final String type; // http://purl.org/dc/terms/type
    private final String selectedText; // http://fise.iks-project.eu/ontology/selected-text
    private final String selectionContext; // http://fise.iks-project.eu/ontology/selection-context
    private final Long start; // http://fise.iks-project.eu/ontology/start
    private final Long end; // http://fise.iks-project.eu/ontology/end
    private final String language; // http://purl.org/dc/terms/language

    /**
     * Constructor
     * 
     * @param resource Jena resource
     */
    public TextAnnotation(Resource resource)
    {
        super(resource);
        this.type = resource.hasProperty(DCTerms.type) ? resource.getPropertyResourceValue(DCTerms.type).getURI() : null;
        this.selectedText = resource.hasProperty(FISE.SELECTED_TEXT) ? resource.getProperty(FISE.SELECTED_TEXT).getString() : null;
        this.selectionContext = resource.hasProperty(FISE.SELECTION_CONTEXT) ? resource.getProperty(FISE.SELECTION_CONTEXT).getString() : null;
        this.start = resource.hasProperty(FISE.START) ? resource.getProperty(FISE.START).getLong() : null;
        this.end = resource.hasProperty(FISE.END) ? resource.getProperty(FISE.END).getLong() : null;
        this.language = resource.hasProperty(DCTerms.language) ? resource.getProperty(DCTerms.language).getString() : null;
    }

    /**
     * Get the dc:type property
     * 
     * @return dc:type property
     */
    public String getType()
    {
        return type;
    }

    /**
     * Get the fise:selected-text property
     * 
     * @return fise:selected-text property
     */
    public String getSelectedText()
    {
        return selectedText;
    }

    /**
     * Get the fise:selection-context property
     * 
     * @return fise:selection-context property
     */
    public String getSelectionContext()
    {
        return selectionContext;
    }

    /**
     * Get the fise:start property
     * 
     * @return fise:start property
     */
    public Long getStart()
    {
        return start;
    }

    /**
     * Get the fise:end property
     * 
     * @return fise:end property
     */
    public Long getEnd()
    {
        return end;
    }

    /**
     * Get dc:language property
     * 
     * @return dc:language property
     */
    public String getLanguage()
    {
        return language;
    }
}
