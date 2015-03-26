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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.stanbol.client.enhancer.impl.EnhancerParameters.OutputFormat;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * Represents the result of Enhancement Services in Stanbol managing the list of resultant Enhancements and the RDF Enhancement Graph
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
* 
 */
public class EnhancementStructure 
{
	
	/**
	 * Enhancement Structure based on Enhancements Relations
	 */
	private Collection<Enhancement> enhancements = Sets.newHashSet(); 

	private Map<String, Entity> entities = Maps.newHashMap();
	
	private Collection<String> languages = Sets.newHashSet();
	
    /**
     * Enhancement Graph
     */
    private Model enhancementGraph;

    /**
     * Constructor
     * 
     * @param enhancementGraph Jena Model containing the Enhancement Graph in RDF format
     */
    private EnhancementStructure(Model enhancementGraph)
    {
        this.enhancementGraph = enhancementGraph;
     }
    
    /**
     * Get the {@link List} of {@link Annotation}s
     * 
     * @return {@link List} of {@link Annotation}s
     */
    public Collection<Enhancement> getEnhancements()
    {
        return enhancements;
    }
    
    /**
     * Get the {@link Collection} of {@link TextAnnotation}s
     * 
     * @return {@link Collection} of {@link TextAnnotation}s
     */
    public Collection<TextAnnotation> getTextAnnotations(){
        return FluentIterable.
        		from(getEnhancements()).
        		filter(new Predicate<Enhancement>(){
					@Override
					public boolean apply(Enhancement input) {
						return input instanceof TextAnnotation;
					}
        			
        		}).
        		transform(new Function<Enhancement, TextAnnotation>(){
					@Override
					public TextAnnotation apply(Enhancement input) {
						return (TextAnnotation) input;
					}
        			
        		}).toSet();
    }
    
    /**
     * Get the {@link Collection} of {@link EntityAnnotation}s
     * 
     * @return {@link Collection} of {@link EntityAnnotation}s
     */
    public Collection<EntityAnnotation> getEntityAnnotations(){
    	return FluentIterable.
        		from(getEnhancements()).
        		filter(new Predicate<Enhancement>(){
					@Override
					public boolean apply(Enhancement input) {
						return input instanceof EntityAnnotation;
					}
        			
        		}).
        		transform(new Function<Enhancement, EntityAnnotation>(){
					@Override
					public EntityAnnotation apply(Enhancement input) {
						return (EntityAnnotation) input;
					}
        			
        		}).toSet();
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
     * Get a {@link Annotation} from the list of Enhancements by its URI
     * 
     * @param URI URI of the {@link Annotation}
     * @return {@link Annotation} within the list identified by its URI
     */
    public Enhancement getEnhancement(final String URI)
    {
    	return FluentIterable.from(getEnhancements()).
    		firstMatch(new Predicate<Enhancement>(){
				@Override
				public boolean apply(Enhancement input) {
					return input.getUri().equals(URI);
				}
    		}).orNull();
    }

    private Enhancement removeEnhancementFromList(String URI)
    {
    	Iterator<Enhancement> it = enhancements.iterator();
    	while(it.hasNext()){
    		Enhancement next = it.next();
    		if(next.getUri().equals(URI)){
    			enhancements.remove(next);
    			return next;
    		}
    	}
        
    	return null;
    }

    /**
     * Remove an {@link Annotation} from the results by its URI
     * 
     * @param enhancementURI {@link Annotation} URI
     */
    public void removeEnhancement(String enhancementURI)
    {

        if (isInTheGraph(enhancementURI))
        {
        	Enhancement enhancement = removeEnhancementFromList(enhancementURI);

            if (enhancement instanceof TextAnnotation){
                removeTextAnnotation(enhancementGraph.getResource(enhancementURI));
            }
            else{
                Resource entityResource = enhancementGraph.getResource(enhancementURI);
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
    public Collection<EntityAnnotation> getEntityAnnotations(final TextAnnotation ta){
    	
    	Collection<EntityAnnotation> eas = getEntityAnnotations();
    	return FluentIterable.
    		from(eas).
    		filter(new Predicate<EntityAnnotation>(){
				@Override
				public boolean apply(EntityAnnotation input) {
					return FluentIterable.
							from(input.getRelation()).
							anyMatch(new Predicate<Enhancement>(){
								@Override
								public boolean apply(Enhancement input) {
									return input.getUri().equals(ta.getUri());
								}
							});
				}
    		}).toSet();
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
    
    /**
     * Returns the {@link Collection} of dereferenced {@link Entity}s
     *
     * @return
     */
    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }
    
    /**
     * Returns a dereferenced entity by its URI
     *
     * @param URI
     * @return
     */
    public Entity getEntity(String URI) {
        return entities.get(URI);
    }

    /**
     * Returns a {@link Collection} of {@link Entity}s for which associated {@link EntityAnnotation}s has a confidence value
     * greater than or equal to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<Entity> getEntitiesByConfidenceValue(final Double confidenceValue) {

        Collection<EntityAnnotation> sortedEas = getEntityAnnotationsByConfidenceValue(confidenceValue);

        return Collections2.transform(sortedEas,
                new Function<EntityAnnotation, Entity>() {
                    @Override
                    public Entity apply(final EntityAnnotation ea) {
                        return ea.getDereferencedEntity();
                    }
                }
        );
    }

    /**
     * Returns a {@link Collection} of {@link TextAnnotation}s which confidences values are greater than or equal
     * to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<TextAnnotation> getTextAnnotationsByConfidenceValue(final Double confidenceValue) {
        return FluentIterable.from(getTextAnnotations())
                .filter(new Predicate<TextAnnotation>() {
                    @Override
                    public boolean apply(TextAnnotation e) {
                        return e.getConfidence().doubleValue() >= confidenceValue
                                .doubleValue();
                    }
                }).toSet();
    }

    /**
     * Returns a {@link Collection} of {@link EntityAnnotation}s which confidences values are greater than or equal
     * to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<EntityAnnotation> getEntityAnnotationsByConfidenceValue(
            final Double confidenceValue) {
        return FluentIterable.from(getEntityAnnotations())
                .filter(new Predicate<EntityAnnotation>() {
                    @Override
                    public boolean apply(EntityAnnotation e) {
                        return e.getConfidence().doubleValue() >= confidenceValue
                                .doubleValue();
                    }
                }).toSet();
    }

    /**
     * Returns all the entity annotations for each text annotation
     * 
     * @return
     */
    public Multimap<TextAnnotation, EntityAnnotation> getEntityAnnotationsByTextAnnotation() {
        Multimap<TextAnnotation, EntityAnnotation> map = ArrayListMultimap.create();

        Collection<EntityAnnotation> eas = getEntityAnnotations();
        for (EntityAnnotation ea : eas) {
            if (ea.getRelation() != null) {
                for (Enhancement e : ea.getRelation()) {
                    if (e instanceof TextAnnotation) {
                        map.put((TextAnnotation) e, ea);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Returns the best {@link EntityAnnotation}s (those with the highest confidence value) for each extracted {@link TextAnnotation}
     *
     * @return best annotations
     */
    public Multimap<TextAnnotation, EntityAnnotation> getBestAnnotations() {

        Ordering<EntityAnnotation> o = new Ordering<EntityAnnotation>() {
            @Override
            public int compare(EntityAnnotation left, EntityAnnotation right) {
                return Doubles.compare(left.getConfidence(), right.getConfidence());
            }
        }.reverse();

        Multimap<TextAnnotation, EntityAnnotation> result = ArrayListMultimap.create();
        for (TextAnnotation ta : getTextAnnotations()) {
            List<EntityAnnotation> eas = o.sortedCopy(getEntityAnnotations(ta));
            if (!eas.isEmpty()) {
                Collection<EntityAnnotation> highest = Sets.newHashSet();
                Double confidence = eas.get(0).getConfidence();
                for (EntityAnnotation ea : eas) {
                    if (ea.getConfidence() < confidence) {
                        break;
                    } else {
                        highest.add(ea);
                    }
                }
                result.putAll(ta, highest);
            }
        }

        return result;
    }

    /**
     * Returns an {@link EntityAnnotation} by its associated dereferenced {@link Entity} URI
     *
     * @param entityUri
     * @return
     */
    public EntityAnnotation getEntityAnnotation(final String entityUri) {
        return Iterables.tryFind(getEntityAnnotations(),
                new Predicate<EntityAnnotation>() {

                    @Override
                    public boolean apply(EntityAnnotation ea) {
                        return ea.getDereferencedEntity().getUri()
                                .equals(entityUri);
                    }

                }
        ).orNull();
    }

    /**
     * Returns a {@link Collection} of identified languages in the analyzed content
     *
     * @return
     */
    public Collection<String> getLanguages() {
        return languages;
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

    private boolean isInTheGraph(final String URI)
    {
    	return FluentIterable.
    			from(getEnhancements()).
    			anyMatch(new Predicate<Enhancement>(){
					@Override
					public boolean apply(Enhancement input) {
						return input.getUri().equals(URI);
					}
    			});
    }
    
    /**
     * String representing the Enhancement Structure in JSON format
     * 
     * @return
     */
    public String toJSONString(){
    	Multimap<TextAnnotation, EntityAnnotation> annotations = getEntityAnnotationsByTextAnnotation();
    	JSONObject result = new JSONObject();
    	Collection<JSONObject> aCol = Lists.newArrayList();
    	try{
    		result.put("languages", getLanguages());
    		for(TextAnnotation nextTA:annotations.keySet()){
    			Collection<EntityAnnotation> eas = annotations.get(nextTA);
    			JSONObject next = new JSONObject();
    			next.put("start", nextTA.getStart());
    			next.put("end", nextTA.getEnd());
    			next.put("language", nextTA.getLanguage());
    			next.put("selected-text", nextTA.getSelectedText());
    			JSONArray entities = new JSONArray();
    			for(EntityAnnotation ea:eas){
    				JSONObject nextEA = new JSONObject();
    				nextEA.put("preferred-label", ea.getEntityLabel());
    				nextEA.put("uri", ea.getEntityReference());
    				nextEA.put("types", ea.getEntityTypes());
    				nextEA.put("site", ea.getSite());

    				JSONObject properties = new JSONObject();
    				Entity entity = ea.getDereferencedEntity();

    				JSONObject labels = new JSONObject();
    				Map<String, String> labelsByLanguage = entity.getLabelsByLanguage();
    				for(Entry<String, String> entry:labelsByLanguage.entrySet()){
    					labels.put(entry.getKey(), entry.getValue());
    				}
    				properties.put("all-labels", labels);

    				JSONObject descriptions = new JSONObject();
    				Map<String, String> descriptionsByLanguage = entity.getCommentsByLanguage();
    				for(Entry<String, String> entry:descriptionsByLanguage.entrySet()){
    					descriptions.put(entry.getKey(), entry.getValue());
    				}
    				properties.put("descriptions", descriptions);

    				properties.put("categories", entity.getCategories());
    				Collection<String> rdfProperties = entity.getProperties();
    				for(String rdfProperty:rdfProperties)
    					properties.put(rdfProperty, entity.getPropertyValues(rdfProperty));

    				nextEA.put("properties", properties);
    				entities.put(nextEA);
    			}
    			next.put("entities", entities);
    			aCol.add(next);
    		}
    		JSONArray annotationsArr = new JSONArray(aCol);
    		result.put("annotations", annotationsArr);
    		return result.toString();
    	} catch (JSONException e){
    		return new JSONArray().toString();
    	}

    }

    /**
     * Filter the {@link Annotation} results by a confidence threshold. This method remove all {@link EntityAnnotation} 
     * with a confidence value lower than a threshold value passed by parameter
     * 
     * @param confidenceThreshold Threshold Value
     */
    public void filterByConfidence(Double confidenceThreshold)
    {
        List<Annotation> toBeRemoved = Lists.newArrayList();

        for (EntityAnnotation ea : getEntityAnnotations())
            if (((Annotation) ea).getConfidence() < confidenceThreshold)
                toBeRemoved.add(ea);

        for (Annotation e : toBeRemoved)
            this.removeEnhancement(e.getUri());

    }
    
//    /**
//     * For each {@link TextAnnotation}, remove all {@link EntityAnnotation}s with a confidence lower than the higher one 
//     */
//    public void disambiguate(){
//        
//        List<EntityAnnotation> toBeRemoved = Lists.newArrayList();
//        for(SortedSet<EntityAnnotation> s:enhancementsMap.values()){
//            Iterator<EntityAnnotation> it = s.iterator();
//            
//            if(it.hasNext())
//                it.next();
//            
//            while(it.hasNext())
//                toBeRemoved.add(it.next());
//        }
//        
//        for (Annotation e : toBeRemoved)
//            this.removeEnhancement(e.getUri());
//    }
    
    @Provider
    @Consumes("*/*")
    public static class EnhancementStructureReader implements MessageBodyReader<EnhancementStructure>{
    	
		@Override
		public boolean isReadable(Class<?> type, Type genericType,
				java.lang.annotation.Annotation[] annotations,
				MediaType mediaType) {
			if(mediaType.isCompatible(OutputFormat.NT.value()) ||
					mediaType.isCompatible(OutputFormat.RDFXML.value()) ||
					mediaType.isCompatible(OutputFormat.TURTLE.value()))
					return true;
			else
				return false;
		}

		@Override
		public EnhancementStructure readFrom(Class<EnhancementStructure> type,
				Type genericType,
				java.lang.annotation.Annotation[] annotations,
				MediaType mediaType,
				MultivaluedMap<String, String> httpHeaders,
				InputStream entityStream) throws IOException,
				WebApplicationException {

	        // Parse the RDF model
	        Model model = ModelFactory.createDefaultModel();
	        String mediaTypeStr = null;
	        if(mediaType.isCompatible(OutputFormat.NT.value()))
	        		mediaTypeStr = "N3";
	        else if(mediaType.isCompatible(OutputFormat.TURTLE.value()))
	        		mediaTypeStr = "TTL";
	        else
	        	mediaTypeStr = "RDF/XML";
	        
	        model.read(entityStream, null, mediaTypeStr);
	        
	        EnhancementStructure result = new EnhancementStructure(model);
	        Collection<Enhancement> enhancements = EnhancementParser.parse(model);
	        result.enhancements = enhancements;
	        
	        Collection<EntityAnnotation> eas = result.getEntityAnnotations();
	        for(EntityAnnotation ea:eas)
	        	result.entities.put(
	        			ea.getDereferencedEntity().getUri(),
	        			ea.getDereferencedEntity());
	        
			Collection<TextAnnotation> tas = result.getTextAnnotations();
			for(TextAnnotation ta:tas)
				result.languages.add(ta.getLanguage());
			
			return result;
		}
    	
    }
}
