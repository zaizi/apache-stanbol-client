package org.zaizi.stanbol.client.model;

import org.zaizi.stanbol.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents a text annotation
 * 
 * @author efoncubierta
 *
 */
@Deprecated
public class TextAnnotation extends Annotation {

	// properties
	private String type;               // http://purl.org/dc/terms/type
	private String selectedText;       // http://fise.iks-project.eu/ontology/selected-text
	private String selectionContext;   // http://fise.iks-project.eu/ontology/selection-context
	private Long start;                // http://fise.iks-project.eu/ontology/start
	private Long end;                  // http://fise.iks-project.eu/ontology/end
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public TextAnnotation(Resource resource) {
		super(resource);
	}
	
	/**
	 * Get the dc:type property
	 * 
	 * @return dc:type property
	 */
	public String getType() {
		if(type == null && resource.hasProperty(DCTerms.type)) {
			type = resource.getPropertyResourceValue(DCTerms.type).getURI();
		}
		return type;
	}

	/**
	 * Get the fise:selected-text property
	 * 
	 * @return fise:selected-text property
	 */
	public String getSelectedText() {
		if(selectedText == null && resource.hasProperty(FISE.SELECTED_TEXT)) {
			selectedText = resource.getProperty(FISE.SELECTED_TEXT).getString();
		}
		return selectedText;
	}

	/**
	 * Get the fise:selection-context property
	 * 
	 * @return fise:selection-context property
	 */
	public String getSelectionContext() {
		if(selectionContext == null && resource.hasProperty(FISE.SELECTION_CONTEXT)) {
			selectionContext = resource.getProperty(FISE.SELECTION_CONTEXT).getString();
		}
		return selectionContext;
	}
	
	/**
	 * Get the fise:start property
	 * 
	 * @return fise:start property
	 */
	public Long getStart() {
		if(start == null && resource.hasProperty(FISE.START)) {
			start = resource.getProperty(FISE.START).getLong();
		}
		return start;
	}
	
	/**
	 * Get the fise:end property
	 * 
	 * @return fise:end property
	 */
	public Long getEnd() {
		if(end == null && resource.hasProperty(FISE.END)) {
			end = resource.getProperty(FISE.END).getLong();
		}
		return end;
	}
}
