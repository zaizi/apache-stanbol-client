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
package org.apache.stanbol.client.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.model.EntityAnnotation;
import org.apache.stanbol.client.model.TextAnnotation;
import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extract enhancements objects from several kind of objects 
 * 
 * @author efoncubierta
 *
 */
public class EnhancementParser {
	
	/**
	 * Parse a Jena model as a list of enhancements
	 * 
	 * @param model Jena model
	 * @return List of enhancements
	 */
	public static List<Enhancement> parse(Model model) {
		List<Enhancement> enhancements = new ArrayList<Enhancement>();
		
		final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, FISE.ENHANCEMENT);
		while(enhancementsIterator.hasNext()) {
			final Resource enhancementResource = enhancementsIterator.next();
			final Enhancement enhancement = parse(enhancementResource);
			
			if(enhancement != null) {
				enhancements.add(enhancement);
			}
		}
		return enhancements;
	}
	
	/**
	 * Parse a Jena resource as an enhancement
	 * 
	 * @param resource Jena resource
	 * @return Enhancement
	 */
	public static Enhancement parse(Resource resource) {
		Enhancement enhancement = null;
		
		if(resource != null) {
			final StmtIterator types = resource.listProperties(RDF.type);
			while(types.hasNext() && enhancement == null) {
				final Statement stmt = types.next();
				
				if(FISE.TEXT_ANNOTATION.equals(stmt.getObject())) {
					enhancement = new TextAnnotation(resource);
				} else if(FISE.ENTITY_ANNOTATION.equals(stmt.getObject())) {
					enhancement = new EntityAnnotation(resource);
				}
			}
		}
		
		return enhancement;
	}
}
