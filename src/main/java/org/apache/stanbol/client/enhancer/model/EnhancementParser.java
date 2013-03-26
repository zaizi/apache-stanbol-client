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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.stanbol.client.contenthub.store.model.DefaultMetadata;
import org.apache.stanbol.client.contenthub.store.model.Metadata;
import org.apache.stanbol.client.ontology.FISE;

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
 * @author Rafa Haro
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
    public static List<Enhancement> parse(Model model)
    {
        List<Enhancement> enhancements = new ArrayList<Enhancement>();

        final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, FISE.ENHANCEMENT);
        while (enhancementsIterator.hasNext())
        {
            final Resource enhancementResource = enhancementsIterator.next();
            final Enhancement enhancement = parse(enhancementResource);

            if (enhancement != null)
            {
                enhancements.add(enhancement);
            }
        }
        return enhancements;
    }
    
    /**
     * PArse a Jena model as a Map of related {@link Annotation}s
     * 
     * @param model Jena Model
     * @return Map of {@link TextAnnotation} - Set of {@link EntityAnnotation}
     */
    public static Map<TextAnnotation, SortedSet<EntityAnnotation>> parseRelations(Model model){
        Map<TextAnnotation, SortedSet<EntityAnnotation>> result = new HashMap<TextAnnotation, SortedSet<EntityAnnotation>>();
        
        Map<String, TextAnnotation> entityMapping = new HashMap<String, TextAnnotation>();
        final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type, FISE.TEXT_ANNOTATION);
        while(enhancementsIterator.hasNext())
        {
            final Resource enhancementResource = enhancementsIterator.next();
            final TextAnnotation annotation = (TextAnnotation) parse(enhancementResource);

            if(annotation != null){
                result.put(annotation, new TreeSet<EntityAnnotation>());
                entityMapping.put(annotation.getUri(), annotation);
            }
        }

        final ResIterator entityIterator = model.listSubjectsWithProperty(RDF.type, FISE.ENTITY_ANNOTATION);
        while(entityIterator.hasNext())
        {
            final Resource entityResource = entityIterator.next();
            final Enhancement entity = parse(entityResource);

            if(entity != null && entity instanceof EntityAnnotation){
                StmtIterator relationIterator = entityResource.listProperties(DCTerms.relation);
                while(relationIterator.hasNext()){
                    Statement nextStatement = relationIterator.next();
                    if(nextStatement.getObject().isResource() && 
                            nextStatement.getObject().asResource().hasProperty(RDF.type, FISE.TEXT_ANNOTATION)){
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
    public static Enhancement parse(Resource resource)
    {
        Enhancement enhancement = null;

        if (resource != null)
        {
            final StmtIterator types = resource.listProperties(RDF.type);
            while (types.hasNext() && enhancement == null)
            {
                final Statement stmt = types.next();

                if (FISE.TEXT_ANNOTATION.equals(stmt.getObject()))
                {
                    enhancement = new TextAnnotation(resource);
                }
                else if (FISE.ENTITY_ANNOTATION.equals(stmt.getObject()))
                {
                    enhancement = new EntityAnnotation(resource);
                }
            }
        }

        return enhancement;
    }

    /**
     * Parse Custom Metadata from Enhancement Graph
     * 
     * @param model Enhancement Graph
     * @return List of Metadata
     */
    public static List<Metadata> parseMetadata(Model model)
    {
        List<Metadata> result = new ArrayList<Metadata>();
        ResIterator iterator = model.listResourcesWithProperty(RDF.type, FISE.USER_ANNOTATION);
        while (iterator.hasNext())
        {
            Resource nextMetadataResource = iterator.next();
            Statement dc = nextMetadataResource.getProperty(DCTerms.creator);
            String creator = null;
            if (dc != null)
                creator = dc.getObject().asLiteral().getString();
            dc = nextMetadataResource.getProperty(DCTerms.type);
            String type = null;
            if (dc != null)
                type = dc.getObject().asLiteral().getString();

            ResIterator metadataIterator = model.listResourcesWithProperty(DCTerms.relation, nextMetadataResource);
            while(metadataIterator.hasNext()){
                Resource nextMetadataValueResource = metadataIterator.next();
                String namespace = nextMetadataValueResource.getNameSpace();
                String name = nextMetadataValueResource.getLocalName();

                List<String> value = new ArrayList<String>();
                StmtIterator sIt = nextMetadataValueResource.listProperties(FISE.USER_METADATA_VALUE);
                while (sIt.hasNext())
                {
                    Statement nextResource = sIt.next();
                    value.add(nextResource.getObject().asLiteral().getString());
                }

                result.add(new DefaultMetadata(name, value, namespace, type, creator, nextMetadataResource.getURI()));
            }
        }

        return result;
    }
}
