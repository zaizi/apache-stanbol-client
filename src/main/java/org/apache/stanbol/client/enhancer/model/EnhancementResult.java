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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents the result of Enhancement Services in Stanbol managing the list of resultant Enhancements and the RDF Enhancement Graph
 * 
 * @author Rafa Haro
 * 
 */
public class EnhancementResult
{

    /**
     * List of Enhancements
     */
    private List<Enhancement> enhancements;
    
    /*
     * TextAnnotations - EntityAnnotations Map
     */
    private Map<TextAnnotation, SortedSet<EntityAnnotation>> enhancementsMap;

    /**
     * Enhancement Graph
     */
    private Model enhancementGraph;

    /**
     * Constructor
     * 
     * @param enhancementGraph Jena Model containing the Enhancement Graph in RDF format
     */
    public EnhancementResult(Model enhancementGraph)
    {
        this.enhancementGraph = enhancementGraph;
        this.enhancementsMap = EnhancementParser.parseRelations(enhancementGraph);
        //this.enhancements = EnhancementParser.parse(enhancementGraph);
        enhancements = new ArrayList<Enhancement>();
        for(Enhancement e:enhancementsMap.keySet())
            enhancements.add(e);
        
        for(SortedSet<EntityAnnotation> ss:enhancementsMap.values())
            for(EntityAnnotation ea:ss){
                if(!enhancements.contains(ea))
                    enhancements.add(ea);
                }
     }
    
    /**
     * Get the {@link List} of {@link Enhancement}s
     * 
     * @return {@link List} of {@link Enhancement}s
     */
    public List<Enhancement> getEnhancements()
    {
        return enhancements;
    }

    /**
     * Set the {@link List} of {@link Enhancement}s
     * 
     * @param enhancements {@link List} of {@link Enhancement}s
     */
    public void setEnhancements(List<Enhancement> enhancements)
    {
        this.enhancements = enhancements;
    }
    
    /**
     * Get the {@link Collection} of {@link TextAnnotation}s
     * 
     * @return {@link Collection} of {@link TextAnnotation}s
     */
    public Collection<TextAnnotation> getTextAnnotations(){
        return enhancementsMap.keySet();
    }
    
    /**
     * Get the {@link Collection} of {@link EntityAnnotation}s
     * 
     * @return {@link Collection} of {@link EntityAnnotation}s
     */
    public Collection<EntityAnnotation> getEntityAnnotations(){
        List<EntityAnnotation> entities = new ArrayList<EntityAnnotation>();
        for(SortedSet<EntityAnnotation> s:enhancementsMap.values())
            entities.addAll(s);
        
        return entities;
    }

    /**
     * Get the Enhancement Graph
     * 
     * @return Jena {@link Model} containing the RDF Enhancement Graph
     */
    public Model getEnhancementGraph()
    {
        return enhancementGraph;
    }

    /**
     * Set the Enhancement Graph
     * 
     * @param enhancementGraph Jena {@link Model} containing the RDF Enhancement Graph
     */
    public void setEnhancementGraph(Model enhancementGraph)
    {
        this.enhancementGraph = enhancementGraph;
    }

    /**
     * Add a new {@link Enhancement} to the Enhancements' List
     * 
     * @param enhancement {@link Enhancement} to be added
     */
    public void addEnhancement(Enhancement enhancement)
    {
        enhancements.add(enhancement);
        
        if((enhancement instanceof TextAnnotation) && !enhancementsMap.containsKey(enhancement))
            enhancementsMap.put((TextAnnotation) enhancement, new TreeSet<EntityAnnotation>());
        else if(enhancement instanceof EntityAnnotation){
            Resource entityResource = enhancementGraph.getResource(enhancement.getUri());
            String taURI = entityResource.getPropertyResourceValue(DC.relation).getURI();
            TextAnnotation ta = (TextAnnotation) getEnhancement(taURI);
            enhancementsMap.get(ta).add((EntityAnnotation) enhancement);
        }
    }

    /**
     * Get a {@link Enhancement} from the list of Enhancements by its URI
     * 
     * @param URI URI of the {@link Enhancement}
     * @return {@link Enhancement} within the list identified by its URI
     */
    public Enhancement getEnhancement(String URI)
    {

        for (Enhancement enhancement : enhancements)
            if (enhancement.getUri().equals(URI))
                return enhancement;

        return null;
    }

    private Enhancement removeEnhancementFromList(String URI)
    {
        for (int i = 0; i < enhancements.size(); i++)
            if (enhancements.get(i).getUri().equals(URI))
                return enhancements.remove(i);

        return null;
    }

    /**
     * Remove an {@link Enhancement} from the results by its URI
     * 
     * @param enhancementURI {@link Enhancement} URI
     */
    public void removeEnhancement(String enhancementURI)
    {

        if (isInTheGraph(enhancementURI))
        {
            Enhancement enhancement = removeEnhancementFromList(enhancementURI);

            if (enhancement instanceof TextAnnotation){
                enhancementsMap.remove(enhancement);
                removeTextAnnotation(enhancementGraph.getResource(enhancementURI));
            }
            else{
                Resource entityResource = enhancementGraph.getResource(enhancementURI);
                String taURI = entityResource.getPropertyResourceValue(DCTerms.relation).getURI();
                TextAnnotation ta = (TextAnnotation) getEnhancement(taURI);
                enhancementsMap.get(ta).remove(enhancement);
                removeEntityAnnotation(entityResource);
            }
        }
    }

    /**
     * Remove a {@link TextAnnotation} enhancement from the results by its URI
     * 
     * @param URI {@link TextAnnotation} URI
     */
    public void removeTextAnnotation(String URI)
    {
        removeEnhancement(URI);
    }

    /**
     * Remove a {@link EntityAnnotation} enhancement from the results by its URI
     * 
     * @param URI {@link EntityAnnotation} URI
     */
    public void removeEntityAnnotation(String URI)
    {
        removeEnhancement(URI);
    }
    
    /**
     * Return the {@link List} of {@link EntityAnnotation}s associated to the {@link TextAnnotation}
     * passed by parameter
     * 
     * @param ta TextAnnotation
     * @return {@link List} of {@link EntityAnnotation}s
     */
    public Collection<EntityAnnotation> getEntityAnnotations(TextAnnotation ta){
        return enhancementsMap.get(ta);
    }
    
    /**
     * Return the {@link List} of {@link EntityAnnotation}s associated to the TextAnnotation
     * which URI is passed by parameter
     * 
     * @param taURI URI of the TextAnnotation
     * @return {@link List} of {@link EntityAnnotation}s
     */
    public Collection<EntityAnnotation> getEntityAnnotations(String taURI){
        Enhancement e = getEnhancement(taURI);
        if(e instanceof TextAnnotation)
            return getEntityAnnotations((TextAnnotation) getEnhancement(taURI));
        else
            return Collections.emptyList();
    }

    private void removeEntityAnnotation(Resource entityAnnotation)
    {
        List<Statement> list = entityAnnotation.listProperties().toList();
        for (Statement nextStatement : list)
            enhancementGraph.remove(nextStatement);       
    }

    private void removeTextAnnotation(Resource enhancement)
    {
        final ResIterator entityIterator = enhancementGraph.listSubjectsWithProperty(DC.relation, enhancement);
        while(entityIterator.hasNext())
            removeEntityAnnotation(entityIterator.next());
                
        List<Statement> list = enhancement.listProperties().toList();
        for (Statement nextStatement : list)
            enhancementGraph.remove(nextStatement);
        
    }

    private boolean isInTheGraph(String URI)
    {
        boolean finded = false;
        for (int i = 0; i < enhancements.size() && !finded; i++)
            finded = enhancements.get(i).getUri().equals(URI);

        return finded;
    }

    /**
     * Filter the {@link Enhancement} results by a confidence threshold. This method remove all {@link EntityAnnotation} 
     * with a confidence value lower than a threshold value passed by parameter
     * 
     * @param confidenceThreshold Threshold Value
     */
    public void filterByConfidence(Double confidenceThreshold)
    {
        List<Enhancement> toBeRemoved = new ArrayList<Enhancement>();

        for (Enhancement e : enhancements)
            if (e instanceof EntityAnnotation && ((Annotation) e).getConfidence() < confidenceThreshold)
                toBeRemoved.add(e);

        for (Enhancement e : toBeRemoved)
            this.removeEnhancement(e.getUri());

    }
    
    /**
     * For each {@link TextAnnotation}, remove all {@link EntityAnnotation}s with a confidence lower than the higher one 
     */
    public void disambiguate(){
        
        List<EntityAnnotation> toBeRemoved = new ArrayList<EntityAnnotation>();
        for(SortedSet<EntityAnnotation> s:enhancementsMap.values()){
            Iterator<EntityAnnotation> it = s.iterator();
            
            if(it.hasNext())
                it.next();
            
            while(it.hasNext())
                toBeRemoved.add(it.next());
        }
        
        for (Enhancement e : toBeRemoved)
            this.removeEnhancement(e.getUri());
    }
}
