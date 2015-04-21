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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.stanbol.client.entityhub.model.Entity;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Utility class for extracting Enhancements and Metadata objects from RDF Graphs
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 * 
 */
public class EnhancementParser
{

    /**
     * Parse a Jena model as a list of enhancements
     * 
     * @param model Jena model
     * @return List of enhancements
     */
    static Collection<Enhancement> parse(Model model)
    {
        Map<String, Enhancement> enhancements = Maps.newHashMap();

        final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, EnhancementStructureOntology.ENHANCEMENT);
        while (enhancementsIterator.hasNext())
        {
            final Resource enhancementResource = enhancementsIterator.next();
            final Enhancement enhancement = parse(enhancementResource);

            if (enhancement != null)
            {
                enhancements.put(enhancementResource.getURI(), enhancement);
            }
        }

        processRelations(enhancements);
        return enhancements.values();
    }
    
    private static void processRelations(Map<String, Enhancement> enhancements){
    	Collection<Enhancement> annotations = enhancements.values();
    	for(Enhancement e:annotations){
    		if (e.resource.hasProperty(DCTerms.relation)){
    			final StmtIterator relationsIterator = e.resource.listProperties(DCTerms.relation);
    			while (relationsIterator.hasNext())
    			{
    				final Statement relationStatement = relationsIterator.next();
    				String relationUri = relationStatement.getObject().asResource().getURI();
    				e.addRelation(enhancements.get(relationUri));
    			}
    		}
    	}
    }
    
    /**
     * PArse a Jena model as a Map of related {@link Annotation}s
     * 
     * @param model Jena Model
     * @return Map of {@link TextAnnotation} - Set of {@link EntityAnnotation}
     */
     static Map<TextAnnotation, SortedSet<EntityAnnotation>> parseRelations(Model model){
        Map<TextAnnotation, SortedSet<EntityAnnotation>> result = new HashMap<TextAnnotation, SortedSet<EntityAnnotation>>();
        
        Map<String, TextAnnotation> entityMapping = new HashMap<String, TextAnnotation>();
        final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, EnhancementStructureOntology.TEXT_ANNOTATION);
        while(enhancementsIterator.hasNext())
        {
            final Resource enhancementResource = enhancementsIterator.next();
            final TextAnnotation annotation = (TextAnnotation) parse(enhancementResource);

            if(annotation != null){
                result.put(annotation, new TreeSet<EntityAnnotation>());
                entityMapping.put(annotation.getUri(), annotation);
            }
        }

        final ResIterator entityIterator = model.listSubjectsWithProperty(RDF.type, EnhancementStructureOntology.ENTITY_ANNOTATION);
        while(entityIterator.hasNext())
        {
            final Resource entityResource = entityIterator.next();
            final Enhancement entity = parse(entityResource);

            if(entity != null && entity instanceof EntityAnnotation){
                StmtIterator relationIterator = entityResource.listProperties(DCTerms.relation);
                while(relationIterator.hasNext()){
                    Statement nextStatement = relationIterator.next();
                    if(nextStatement.getObject().isResource() && 
                            nextStatement.getObject().asResource().hasProperty(RDF.type, EnhancementStructureOntology.TEXT_ANNOTATION)){
                        String taURI = nextStatement.getObject().asResource().getURI();
                        SortedSet<EntityAnnotation> list = result.get(entityMapping.get(taURI));
                        list.add((EntityAnnotation) entity);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Parse a Jena resource as an enhancement
     * 
     * @param resource Jena resource
     * @return Enhancement
     */
    private static Enhancement parse(Resource resource)
    {
        Enhancement enhancement = null;

        if (resource != null)
        {
            final StmtIterator types = resource.listProperties(RDF.type);
            while (types.hasNext() && enhancement == null)
            {
                final Statement stmt = types.next();

                if (EnhancementStructureOntology.TEXT_ANNOTATION.equals(stmt.getObject()))
                {
                    enhancement = new TextAnnotation(resource);
                }
                else if (EnhancementStructureOntology.ENTITY_ANNOTATION.equals(stmt.getObject()))
                {
                    enhancement = new EntityAnnotation(resource, parseEntity(resource));
                }
            }
        }

        return enhancement;
    }

	private static Entity parseEntity(Resource eaUri) {
		if(eaUri.hasProperty(EnhancementStructureOntology.ENTITY_REFERENCE)){
			
			final Resource entity = 
					eaUri.getPropertyResourceValue(EnhancementStructureOntology.ENTITY_REFERENCE); // Should be only one
			
			String siteStr = null;
			Statement site = eaUri.getProperty(EnhancementStructureOntology.ENTITYHUB_SITE);
			if(site != null)
				siteStr = site.getObject().asLiteral().toString();
			return new Entity(entity, siteStr);
		}
		
		return null;
	}
}
