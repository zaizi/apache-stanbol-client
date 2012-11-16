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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.stanbol.client.model.parser.EnhancementParser;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents an enhancement
 * 
 * @author efoncubierta
 *
 */
public abstract class Enhancement {	
	
	protected Resource resource;
	
	// properties
	private String uri;
	private Date created;                // http://purl.org/dc/terms/created
	private String creator;              // http://purl.org/dc/terms/creator
	private List<Enhancement> relation;  // http://purl.org/dc/terms/relation
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public Enhancement(Resource resource) {
		this.resource = resource;
		this.uri = resource.getURI();
	}
	
	/**
	 * Get resource URI
	 * 
	 * @return Resource URI
	 */
	public String getUri() {
	    return uri;
	}
	
	/**
	 * Get the dc:creator property
	 * 
	 * @return dc:creator property
	 */
	public String getCreator() {
		if(creator == null && resource.hasProperty(DCTerms.creator)) {
			creator = resource.getProperty(DCTerms.creator).getString();
		}
		return creator;
	}
	
	/**
	 * Get the dc:created property
	 * 
	 * @return dc:created property
	 */
	public Date getCreated() {
		if(created == null && resource.hasProperty(DCTerms.created)) {
			// TODO extract data
			created = new Date();
		}
		return created;
	}
	
	/**
	 * Get the dc:relation property
	 * 
	 * @return dc:relation property
	 */
	public List<Enhancement> getRelation() {
		if(relation == null && resource.hasProperty(DCTerms.relation)) {
			relation = new ArrayList<Enhancement>();
			
			final StmtIterator relationsIterator = resource.listProperties(DCTerms.relation);
			while(relationsIterator.hasNext()) {
				final Statement relationStatement = relationsIterator.next();
				
				relation.add(EnhancementParser.parse(relationStatement.getObject().asResource()));
			}
		}
		return relation;
	}
}
