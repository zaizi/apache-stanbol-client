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

import org.apache.stanbol.client.model.Entity;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extract entity objects from several kind of objects 
 * 
 * @author rmartin
 *
 */
public class EntityParser {
	
	/**
	 * Parse a Jena model as a list of entities
	 * 
	 * @param model Jena model
	 * @return List of enhancements
	 */
	public static List<Entity> parse(Model model) {
		List<Entity> entities = new ArrayList<Entity>();
		
		final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, OWL.Thing);
		while(enhancementsIterator.hasNext()) {
		    Resource next = enhancementsIterator.next();
			entities.add(new Entity(next.getModel(), next.getURI()));
		}
		return entities;
	}
	
}
