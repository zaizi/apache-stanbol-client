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
 * Represents an entity annotation
 * 
 * @author efoncubierta
 *
 */
public class EntityAnnotation extends Annotation {
	
	// properties
	private String entityLabel ;       // http://fise.iks-project.eu/ontology/entity-label
	private String entityReference;    // http://fise.iks-project.eu/ontology/entity-reference
	private String entityType;         // http://fise.iks-project.eu/ontology/entity-type
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public EntityAnnotation(Resource resource) {
		super(resource);
	}

	/**
	 * Get the fise:entity-label property
	 * 
	 * @return fise:entity-label property
	 */
	public String getEntityLabel() {
		if(entityLabel == null && resource.hasProperty(FISE.ENTITY_LABEL)) {
			entityLabel = resource.getProperty(FISE.ENTITY_LABEL).getString();
		}
		return entityLabel;
	}

	/**
	 * Get the fise:entity-reference property
	 * 
	 * @return fise:entity-reference property
	 */
	public String getEntityReference() {
		if(entityReference == null && resource.hasProperty(FISE.ENTITY_REFERENCE)) {
			entityReference = resource.getPropertyResourceValue(FISE.ENTITY_REFERENCE).getURI();
		}
		return entityReference;
	}
	
	/**
	 * Get the fise:entity-type property
	 * 
	 * @return fise:entity-type property
	 */
	public String getEntityType() {
		if(entityType == null && resource.hasProperty(FISE.ENTITY_TYPE)) {
			entityType = resource.getPropertyResourceValue(FISE.ENTITY_TYPE).getURI();
		}
		return entityType;
	}
}
