package org.zaizi.stanbol.client.model;

import org.zaizi.stanbol.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Represents an entity annotation
 * 
 * @author efoncubierta
 *
 */
@Deprecated
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
