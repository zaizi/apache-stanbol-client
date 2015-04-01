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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents a text annotation from FISE ontology
 * 
 * @author efoncubierta
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
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
    TextAnnotation(Resource resource)
    {
        super(resource);
        this.type = resource.hasProperty(DCTerms.type) ? resource.getPropertyResourceValue(DCTerms.type).getURI() : null;
        this.selectedText = resource.hasProperty(EnhancementStructureOntology.SELECTED_TEXT) ? resource.getProperty(EnhancementStructureOntology.SELECTED_TEXT).getString() : null;
        this.selectionContext = resource.hasProperty(EnhancementStructureOntology.SELECTION_CONTEXT) ? resource.getProperty(EnhancementStructureOntology.SELECTION_CONTEXT).getString() : null;
        this.start = resource.hasProperty(EnhancementStructureOntology.START) ? resource.getProperty(EnhancementStructureOntology.START).getLong() : null;
        this.end = resource.hasProperty(EnhancementStructureOntology.END) ? resource.getProperty(EnhancementStructureOntology.END).getLong() : null;
        if (resource.hasProperty(DCTerms.language)) {
        	this.language = resource.getProperty(DCTerms.language).getString();
        } else if (resource.hasProperty(EnhancementStructureOntology.SELECTED_TEXT)) {
        	this.language = resource.getProperty(EnhancementStructureOntology.SELECTED_TEXT).getLanguage();
        } else {
        	this.language = null;
        }
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
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TextAnnotation [getType()=");
		builder.append(getType());
		builder.append(", getSelectedText()=");
		builder.append(getSelectedText());
		builder.append(", getSelectionContext()=");
		builder.append(getSelectionContext());
		builder.append(", getStart()=");
		builder.append(getStart());
		builder.append(", getEnd()=");
		builder.append(getEnd());
		builder.append(", getLanguage()=");
		builder.append(getLanguage());
		builder.append(", getExtractedFrom()=");
		builder.append(getExtractedFrom());
		builder.append(", getConfidence()=");
		builder.append(getConfidence());
		builder.append(", getUri()=");
		builder.append(getUri());
		builder.append(", getCreator()=");
		builder.append(getCreator());
		builder.append(", getCreated()=");
		builder.append(getCreated());
		builder.append(", getRelation()=");
		builder.append(getRelation());
		builder.append("]");
		return builder.toString();
	}
    
}
