package org.zaizi.stanbol.client.model;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Represent a content item
 * 
 * @author efoncubierta
 *
 */
public class ContentItem {
	
	private Model model;
	private String id;
	private List<Enhancement> enhancements;
	
	/**
	 * Constructor
	 * 
	 * @param id Content id
	 * @param enhancements Content enhancements
	 */
	public ContentItem(String id, Model model) {
		this.id = id;
		this.model = model;
	}
	
	/**
	 * Get the content id
	 * 
	 * @return Content id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Get the content enhancements
	 * 
	 * @return Content enhancements
	 */
	public List<Enhancement> getEnhancements() {
		if(enhancements == null) {
			this.enhancements = EnhancementParser.parse(model);
		}
		return enhancements;
	}
}
