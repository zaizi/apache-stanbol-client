package org.zaizi.stanbol.client.model;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Represents a metadata ocurrence
 * 
 * @author efoncubierta
 *
 */
public class MetadataOcurrence extends Ocurrence {

	// properties
	private final String field;
	private final String value;
	
	public MetadataOcurrence(Resource resource) {
		super(resource);
		this.field = "";
		this.value = "";
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}
}
