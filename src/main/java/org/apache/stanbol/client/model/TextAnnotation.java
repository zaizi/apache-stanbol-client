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
package org.apache.stanbol.client.model;

import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents a text annotation
 * 
 * @author efoncubierta
 *
 */
public class TextAnnotation extends Annotation {

	// properties
	private String type;               // http://purl.org/dc/terms/type
	private String selectedText;       // http://fise.iks-project.eu/ontology/selected-text
	private String selectionContext;   // http://fise.iks-project.eu/ontology/selection-context
	private Long start;                // http://fise.iks-project.eu/ontology/start
	private Long end;                  // http://fise.iks-project.eu/ontology/end
	private String language;           // http://purl.org/dc/terms/language
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public TextAnnotation(Resource resource) {
		super(resource);
	}
	
	/**
	 * Get the dc:type property
	 * 
	 * @return dc:type property
	 */
	public String getType() {
		if(type == null && resource.hasProperty(DCTerms.type)) {
			type = resource.getPropertyResourceValue(DCTerms.type).getURI();
		}
		return type;
	}

	/**
	 * Get the fise:selected-text property
	 * 
	 * @return fise:selected-text property
	 */
	public String getSelectedText() {
		if(selectedText == null && resource.hasProperty(FISE.SELECTED_TEXT)) {
			selectedText = resource.getProperty(FISE.SELECTED_TEXT).getString();
		}
		return selectedText;
	}

	/**
	 * Get the fise:selection-context property
	 * 
	 * @return fise:selection-context property
	 */
	public String getSelectionContext() {
		if(selectionContext == null && resource.hasProperty(FISE.SELECTION_CONTEXT)) {
			selectionContext = resource.getProperty(FISE.SELECTION_CONTEXT).getString();
		}
		return selectionContext;
	}
	
	/**
	 * Get the fise:start property
	 * 
	 * @return fise:start property
	 */
	public Long getStart() {
		if(start == null && resource.hasProperty(FISE.START)) {
			start = resource.getProperty(FISE.START).getLong();
		}
		return start;
	}
	
	/**
	 * Get the fise:end property
	 * 
	 * @return fise:end property
	 */
	public Long getEnd() {
		if(end == null && resource.hasProperty(FISE.END)) {
			end = resource.getProperty(FISE.END).getLong();
		}
		return end;
	}
	
	/**
	 * Get dc:language property
	 * 
	 * @return dc:language property
	 */
	public String getLanguage() {
	    if(language == null && resource.hasProperty(DCTerms.language)) {
	        language = resource.getProperty(DCTerms.language).getString();
	    }
	    return language;
	}
}
