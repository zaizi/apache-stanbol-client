package org.zaizi.stanbol.client.model;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Represents an ocurrence
 * 
 * @author efoncubierta
 *
 */
public abstract class Ocurrence extends Enhancement {

	// properties
	private final String type;
	
	public Ocurrence(Resource resource) {
		super(resource);
		type = "";
	}

	public String getType() {
		return type;
	}
}
