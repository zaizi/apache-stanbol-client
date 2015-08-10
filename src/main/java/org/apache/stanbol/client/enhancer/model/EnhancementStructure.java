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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.hp.hpl.jena.vocabulary.DC_11;

/**
 * Represents the result of Enhancement Services in Stanbol managing the list of
 * resultant Enhancements and the RDF Enhancement Graph
 *
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 *
 */
public class EnhancementStructure {

	@Provider
	@Consumes("*/*")
	public static class EnhancementStructureReader implements
			MessageBodyReader<EnhancementStructure> {

		@Override
		public boolean isReadable(final Class<?> type, final Type genericType,
				final java.lang.annotation.Annotation[] annotations,
				final MediaType mediaType) {
			if (mediaType.isCompatible(OutputFormat.NT.value())
					|| mediaType.isCompatible(OutputFormat.RDFXML.value())
					|| mediaType.isCompatible(OutputFormat.TURTLE.value())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public EnhancementStructure readFrom(
				final Class<EnhancementStructure> type, final Type genericType,
				final java.lang.annotation.Annotation[] annotations,
				final MediaType mediaType,
				final MultivaluedMap<String, String> httpHeaders,
				final InputStream entityStream) throws IOException,
				WebApplicationException {

			// Parse the RDF model
			final Model model = ModelFactory.createDefaultModel();
			String mediaTypeStr = null;
			if (mediaType.isCompatible(OutputFormat.NT.value())) {
				mediaTypeStr = "N3";
			} else if (mediaType.isCompatible(OutputFormat.TURTLE.value())) {
				mediaTypeStr = "TTL";
			} else {
				mediaTypeStr = "RDF/XML";
			}

			model.read(entityStream, null, mediaTypeStr);

			final EnhancementStructure result = new EnhancementStructure(model);
			final Collection<Enhancement> enhancements = EnhancementParser
					.parse(model);
			result.enhancements = enhancements;

			final Collection<EntityAnnotation> eas = result
					.getEntityAnnotations();
			for (final EntityAnnotation ea : eas) {
				result.entities.put(ea.getDereferencedEntity().getUri(),
						ea.getDereferencedEntity());
			}

			final Collection<TextAnnotation> tas = result.getTextAnnotations();
			for (final TextAnnotation ta : tas) {
				result.languages.add(ta.getLanguage());
			}

			return result;
		}

	}

	private static JSONObject toJSON(final TextAnnotation nextTA,
			final Collection<EntityAnnotation> entityAnnotations)
			throws JSONException {
		// TODO: create attribute Map and then pass map to "result" in order to optimize map capacity/load factor, etc.
		final JSONObject result = new JSONObject();

		result.put("start", nextTA.getStart());
		result.put("end", nextTA.getEnd());
		result.put("language", nextTA.getLanguage());
		result.put("selected-text", nextTA.getSelectedText());
		result.put("confidence", nextTA.getConfidence());
		result.put("type", nextTA.getType());

		final List<JSONObject> entities = new ArrayList<>(entityAnnotations.size());
		for (final EntityAnnotation ea : entityAnnotations) {
			final JSONObject nextEA = ea.toJSON();
			entities.add(nextEA);
		}
		result.put("entities", new JSONArray(entities));

		return result;
	}

	/**
	 * Enhancement Structure based on Enhancements Relations
	 */
	private Collection<Enhancement> enhancements = Sets.newHashSet();

	private final Map<String, Entity> entities = Maps.newHashMap();

	private final Collection<String> languages = Sets.newHashSet();

	/**
	 * Enhancement Graph
	 */
	private Model enhancementGraph;

	/**
	 * Constructor
	 * 
	 * @param enhancementGraph
	 *            Jena Model containing the Enhancement Graph in RDF format
	 */
	private EnhancementStructure(final Model enhancementGraph) {
		this.enhancementGraph = enhancementGraph;
	}

	/**
	 * Filter the {@link Annotation} results by a confidence threshold. This
	 * method remove all {@link EntityAnnotation} with a confidence value lower
	 * than a threshold value passed by parameter
	 * 
	 * @param confidenceThreshold
	 *            Threshold Value
	 */
	public void filterByConfidence(final Double confidenceThreshold) {
		final List<Annotation> toBeRemoved = Lists.newArrayList();

		for (final EntityAnnotation ea : getEntityAnnotations()) {
			if (((Annotation) ea).getConfidence() < confidenceThreshold) {
				toBeRemoved.add(ea);
			}
		}

		for (final Annotation e : toBeRemoved) {
			removeEnhancement(e.getUri());
		}

	}

	/**
	 * Returns the best {@link EntityAnnotation}s (those with the highest
	 * confidence value) for each extracted {@link TextAnnotation}
	 *
	 * @return best annotations
	 */
	public Multimap<TextAnnotation, EntityAnnotation> getBestAnnotations() {

		final Ordering<EntityAnnotation> o = new Ordering<EntityAnnotation>() {
			@Override
			public int compare(final EntityAnnotation left,
					final EntityAnnotation right) {
				return Doubles.compare(left.getConfidence(),
						right.getConfidence());
			}
		}.reverse();

		final Multimap<TextAnnotation, EntityAnnotation> result = ArrayListMultimap
				.create();
		for (final TextAnnotation ta : getTextAnnotations()) {
			final List<EntityAnnotation> eas = o
					.sortedCopy(getEntityAnnotations(ta));
			if (!eas.isEmpty()) {
				final Collection<EntityAnnotation> highest = Sets.newHashSet();
				final Double confidence = eas.get(0).getConfidence();
				for (final EntityAnnotation ea : eas) {
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
	 * Get a {@link Annotation} from the list of Enhancements by its URI
	 * 
	 * @param URI
	 *            URI of the {@link Annotation}
	 * @return {@link Annotation} within the list identified by its URI
	 */
	public Enhancement getEnhancement(final String URI) {
		return FluentIterable.from(getEnhancements())
				.firstMatch(new Predicate<Enhancement>() {
					@Override
					public boolean apply(final Enhancement input) {
						return input.getUri().equals(URI);
					}
				}).orNull();
	}

	/**
	 * Get the Enhancement Graph
	 * 
	 * @return Jena {@link Model} containing the RDF Enhancement Graph
	 */
	public Model getEnhancementGraph() {
		return enhancementGraph;
	}

	/**
	 * Get the {@link List} of {@link Annotation}s
	 * 
	 * @return {@link List} of {@link Annotation}s
	 */
	public Collection<Enhancement> getEnhancements() {
		return enhancements;
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
	 * Returns a {@link Collection} of {@link Entity}s for which associated
	 * {@link EntityAnnotation}s has a confidence value greater than or equal to
	 * the value passed by parameter
	 *
	 * @param confidenceValue
	 *            Threshold confidence value
	 * @return
	 */
	public Collection<Entity> getEntitiesByConfidenceValue(
			final Double confidenceValue) {

		final Collection<EntityAnnotation> sortedEas = getEntityAnnotationsByConfidenceValue(confidenceValue);

		return Collections2.transform(sortedEas,
				new Function<EntityAnnotation, Entity>() {
					@Override
					public Entity apply(final EntityAnnotation ea) {
						return ea.getDereferencedEntity();
					}
				});
	}

	/**
	 * Returns a dereferenced entity by its URI
	 *
	 * @param URI
	 * @return
	 */
	public Entity getEntity(final String URI) {
		return entities.get(URI);
	}

	/**
	 * Returns an {@link EntityAnnotation} by its associated dereferenced
	 * {@link Entity} URI
	 *
	 * @param entityUri
	 * @return
	 */
	public EntityAnnotation getEntityAnnotation(final String entityUri) {
		return Iterables.tryFind(getEntityAnnotations(),
				new Predicate<EntityAnnotation>() {

					@Override
					public boolean apply(final EntityAnnotation ea) {
						return ea.getDereferencedEntity().getUri()
								.equals(entityUri);
					}

				}).orNull();
	}

	/**
	 * Get the {@link Collection} of {@link EntityAnnotation}s
	 * 
	 * @return {@link Collection} of {@link EntityAnnotation}s
	 */
	public Collection<EntityAnnotation> getEntityAnnotations() {
		return FluentIterable.from(getEnhancements())
				.filter(new Predicate<Enhancement>() {
					@Override
					public boolean apply(final Enhancement input) {
						return input instanceof EntityAnnotation;
					}

				}).transform(new Function<Enhancement, EntityAnnotation>() {
					@Override
					public EntityAnnotation apply(final Enhancement input) {
						return (EntityAnnotation) input;
					}

				}).toSet();
	}

	/**
	 * Return the {@link List} of {@link EntityAnnotation}s associated to the
	 * TextAnnotation which URI is passed by parameter
	 * 
	 * @param taURI
	 *            URI of the TextAnnotation
	 * @return {@link List} of {@link EntityAnnotation}s
	 */
	public Collection<EntityAnnotation> getEntityAnnotations(final String taURI) {
		final Enhancement e = getEnhancement(taURI);
		if (e instanceof TextAnnotation) {
			return getEntityAnnotations((TextAnnotation) getEnhancement(taURI));
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Return the {@link List} of {@link EntityAnnotation}s associated to the
	 * {@link TextAnnotation} passed by parameter
	 * 
	 * @param ta
	 *            TextAnnotation
	 * @return {@link List} of {@link EntityAnnotation}s
	 */
	public Collection<EntityAnnotation> getEntityAnnotations(
			final TextAnnotation ta) {

		final Collection<EntityAnnotation> eas = getEntityAnnotations();
		return FluentIterable.from(eas)
				.filter(new Predicate<EntityAnnotation>() {
					@Override
					public boolean apply(final EntityAnnotation input) {
						return FluentIterable.from(input.getRelation())
								.anyMatch(new Predicate<Enhancement>() {
									@Override
									public boolean apply(final Enhancement input) {
										return input.getUri().equals(
												ta.getUri());
									}
								});
					}
				}).toSet();
	}

	/**
	 * Returns a {@link Collection} of {@link EntityAnnotation}s which
	 * confidences values are greater than or equal to the value passed by
	 * parameter
	 *
	 * @param confidenceValue
	 *            Threshold confidence value
	 * @return
	 */
	public Collection<EntityAnnotation> getEntityAnnotationsByConfidenceValue(
			final Double confidenceValue) {
		return FluentIterable.from(getEntityAnnotations())
				.filter(new Predicate<EntityAnnotation>() {
					@Override
					public boolean apply(final EntityAnnotation e) {
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
		final Multimap<TextAnnotation, EntityAnnotation> map = ArrayListMultimap
				.create();

		final Collection<EntityAnnotation> eas = getEntityAnnotations();
		for (final EntityAnnotation ea : eas) {
			if (ea.getRelation() != null) {
				for (final Enhancement e : ea.getRelation()) {
					if (e instanceof TextAnnotation) {
						map.put((TextAnnotation) e, ea);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Returns a {@link Collection} of identified languages in the analyzed
	 * content
	 *
	 * @return
	 */
	public Collection<String> getLanguages() {
		return languages;
	}

	/**
	 * Get the {@link Collection} of {@link TextAnnotation}s
	 * 
	 * @return {@link Collection} of {@link TextAnnotation}s
	 */
	public Collection<TextAnnotation> getTextAnnotations() {
		return FluentIterable.from(getEnhancements())
				.filter(new Predicate<Enhancement>() {
					@Override
					public boolean apply(final Enhancement input) {
						return input instanceof TextAnnotation;
					}

				}).transform(new Function<Enhancement, TextAnnotation>() {
					@Override
					public TextAnnotation apply(final Enhancement input) {
						return (TextAnnotation) input;
					}

				}).toSet();
	}

	/**
	 * Returns a {@link Collection} of {@link TextAnnotation}s which confidences
	 * values are greater than or equal to the value passed by parameter
	 *
	 * @param confidenceValue
	 *            Threshold confidence value
	 * @return
	 */
	public Collection<TextAnnotation> getTextAnnotationsByConfidenceValue(
			final Double confidenceValue) {
		return FluentIterable.from(getTextAnnotations())
				.filter(new Predicate<TextAnnotation>() {
					@Override
					public boolean apply(final TextAnnotation e) {
						return e.getConfidence().doubleValue() >= confidenceValue
								.doubleValue();
					}
				}).toSet();
	}

	/**
	 * Remove an {@link Annotation} from the results by its URI
	 * 
	 * @param enhancementURI
	 *            {@link Annotation} URI
	 */
	public void removeEnhancement(final String enhancementURI) {

		if (isInTheGraph(enhancementURI)) {
			final Enhancement enhancement = removeEnhancementFromList(enhancementURI);

			if (enhancement instanceof TextAnnotation) {
				removeTextAnnotation(enhancementGraph
						.getResource(enhancementURI));
			} else {
				final Resource entityResource = enhancementGraph
						.getResource(enhancementURI);
				removeEntityAnnotation(entityResource);
			}
		}
	}

	/**
	 * Remove a {@link EntityAnnotation} enhancement from the results by its URI
	 * 
	 * @param URI
	 *            {@link EntityAnnotation} URI
	 */
	public void removeEntityAnnotation(final String URI) {
		removeEnhancement(URI);
	}

	/**
	 * Remove a {@link TextAnnotation} enhancement from the results by its URI
	 * 
	 * @param URI
	 *            {@link TextAnnotation} URI
	 */
	public void removeTextAnnotation(final String URI) {
		removeEnhancement(URI);
	}

	/**
	 * Set the Enhancement Graph
	 * 
	 * @param enhancementGraph
	 *            Jena {@link Model} containing the RDF Enhancement Graph
	 */
	public void setEnhancementGraph(final Model enhancementGraph) {
		this.enhancementGraph = enhancementGraph;
	}

	public JSONObject toJSON() throws JSONException {
		final Multimap<TextAnnotation, EntityAnnotation> annotations = getEntityAnnotationsByTextAnnotation();
		final JSONObject result = new JSONObject();
		final Set<TextAnnotation> textAnnotations = annotations.keySet();
		final List<JSONObject> aCol = new ArrayList<>(textAnnotations.size());

		result.put("languages", getLanguages());
		for (final TextAnnotation nextTA : textAnnotations) {
			final Collection<EntityAnnotation> entityAnnotations = annotations
					.get(nextTA);
			final JSONObject textEntityAnnotationMapping = toJSON(nextTA,
					entityAnnotations);
			aCol.add(textEntityAnnotationMapping);
		}
		final JSONArray annotationsArr = new JSONArray(aCol);
		result.put("annotations", annotationsArr);
		return result;

	}

	/**
	 * 
	 * @return String representing the Enhancement Structure in JSON format
	 */
	public String toJSONString() {
		Object json;
		try {
			json = toJSON();
			
		} catch (final JSONException e) {
			json = new JSONArray();
		}

		return json.toString();
	}

	private boolean isInTheGraph(final String URI) {
		return FluentIterable.from(getEnhancements()).anyMatch(
				new Predicate<Enhancement>() {
					@Override
					public boolean apply(final Enhancement input) {
						return input.getUri().equals(URI);
					}
				});
	}

	private Enhancement removeEnhancementFromList(final String URI) {
		final Iterator<Enhancement> it = enhancements.iterator();
		while (it.hasNext()) {
			final Enhancement next = it.next();
			if (next.getUri().equals(URI)) {
				enhancements.remove(next);
				return next;
			}
		}

		return null;
	}

	private void removeEntityAnnotation(final Resource entityAnnotation) {
		final List<Statement> list = entityAnnotation.listProperties().toList();
		for (final Statement nextStatement : list) {
			enhancementGraph.remove(nextStatement);
		}
	}

	// /**
	// * For each {@link TextAnnotation}, remove all {@link EntityAnnotation}s
	// with a confidence lower than the higher one
	// */
	// public void disambiguate(){
	//
	// List<EntityAnnotation> toBeRemoved = Lists.newArrayList();
	// for(SortedSet<EntityAnnotation> s:enhancementsMap.values()){
	// Iterator<EntityAnnotation> it = s.iterator();
	//
	// if(it.hasNext())
	// it.next();
	//
	// while(it.hasNext())
	// toBeRemoved.add(it.next());
	// }
	//
	// for (Annotation e : toBeRemoved)
	// this.removeEnhancement(e.getUri());
	// }

	private void removeTextAnnotation(final Resource enhancement) {
		final ResIterator entityIterator = enhancementGraph
				.listSubjectsWithProperty(DC_11.relation, enhancement);
		while (entityIterator.hasNext()) {
			removeEntityAnnotation(entityIterator.next());
		}

		final List<Statement> list = enhancement.listProperties().toList();
		for (final Statement nextStatement : list) {
			enhancementGraph.remove(nextStatement);
		}

	}
}
