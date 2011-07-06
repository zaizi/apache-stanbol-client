package org.zaizi.stanbol.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

public class FISE {

	public static final String URI = "http://fise.iks-project.eu/ontology/";
	
	public static final Property EXTRACTED_FROM = new PropertyImpl(URI + "extracted-from");
	public static final Property CONFIDENCE = new PropertyImpl(URI + "confidence");
	
	public static final Property ENTITY_LABEL = new PropertyImpl(URI + "entity-label");
	public static final Property ENTITY_REFERENCE = new PropertyImpl(URI + "entity-reference");
	public static final Property ENTITY_TYPE = new PropertyImpl(URI + "entity-type");
	
	public static final Property SELECTED_TEXT = new PropertyImpl(URI + "selected-text");
	public static final Property SELECTION_CONTEXT = new PropertyImpl(URI + "selection-context");
	public static final Property START = new PropertyImpl(URI + "start");
	public static final Property END = new PropertyImpl(URI + "end");
}
