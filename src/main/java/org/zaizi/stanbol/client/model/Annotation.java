package org.zaizi.stanbol.client.model;

import org.zaizi.stanbol.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Represents an annotation
 * 
 * @author efoncubierta
 *
 */
public class Annotation extends Enhancement {

	// properties
	private String extractedFrom;    // http://fise.iks-project.eu/ontology/extracted-from
	private Double confidence;       // http://fise.iks-project.eu/ontology/confidence
	
	// proposed properties
//	private String title;
//	private String role;
//
//	private String entity;
//	private String entityType;
//	private List<Annotation> suggestions;
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public Annotation(Resource resource) {
		super(resource);
		
//		this.title = "";
//		this.role = "";
//		this.entity = "";
//		this.entityType = "";
//		this.suggestions = new ArrayList<Annotation>();
	}

	/**
	 * Get the fise:extracted-from property
	 * 
	 * @return fise:extracted-from property
	 */
	public String getExtractedFrom() {
		if(extractedFrom == null && resource.hasProperty(FISE.EXTRACTED_FROM)) {
			extractedFrom = resource.getPropertyResourceValue(FISE.EXTRACTED_FROM).getURI();
		}
		return extractedFrom;
	}
	
	/**
	 * Get the fise:confidence property
	 * 
	 * @return fise:confidence property
	 */
	public Double getConfidence() {
		if(confidence == null && resource.hasProperty(FISE.CONFIDENCE)) {
			confidence = resource.getProperty(FISE.CONFIDENCE).getDouble();
		}
		return confidence;
	}
	
//
//	public String getTitle() {
//		return title;
//	}
//
//	public String getRole() {
//		return role;
//	}
// 
//	public String getEntity() {
//		return entity;
//	}
//
//	public String getEntityType() {
//		return entityType;
//	}
//
//	public List<Annotation> getSuggestions() {
//		return suggestions;
//	}
}
