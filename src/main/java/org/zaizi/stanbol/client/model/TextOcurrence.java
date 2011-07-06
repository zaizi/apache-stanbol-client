package org.zaizi.stanbol.client.model;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Represents a text ocurrence
 * 
 * @author efoncubierta
 *
 */
public class TextOcurrence extends Enhancement {

	// properties
	private final String selectedText;
	private final Long start;
	private final Long end;
	private final String context;
	private final Integer ocurrenceWithinContext;
	
	public TextOcurrence(Resource resource) {
		super(resource);
		this.selectedText = "";
		this.start = 0L;
		this.end = 0L;
		this.context = "";
		this.ocurrenceWithinContext = 0;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public Long getStart() {
		return start;
	}

	public Long getEnd() {
		return end;
	}

	public String getContext() {
		return context;
	}

	public Integer getOcurrenceWithinContext() {
		return ocurrenceWithinContext;
	}
}
