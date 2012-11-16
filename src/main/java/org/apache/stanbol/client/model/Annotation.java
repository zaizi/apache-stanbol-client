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

/**
 * Represents an annotation
 * 
 * @author efoncubierta
 *
 */
public class Annotation extends Enhancement {

	// properties
	private String extractedFrom;    // http://fise.iks-project.eu/ontology/extracted-from
	private Double confidence;       // http://fise.iks-project.eu/ontology/confidence
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public Annotation(Resource resource) {
		super(resource);
	}

	/**
	 * Get the fise:extracted-from property
	 * 
	 * @return fise:extracted-from property
	 */
	public String getExtractedFrom() {
		if(extractedFrom == null && resource.hasProperty(FISE.EXTRACTED_FROM)) {
			extractedFrom = resource.getPropertyResourceValue(FISE.EXTRACTED_FROM).getURI();
		}
		return extractedFrom;
	}
	
	/**
	 * Get the fise:confidence property
	 * 
	 * @return fise:confidence property
	 */
	public Double getConfidence() {
		if(confidence == null && resource.hasProperty(FISE.CONFIDENCE)) {
			confidence = resource.getProperty(FISE.CONFIDENCE).getDouble();
		}
		return confidence;
	}
}
