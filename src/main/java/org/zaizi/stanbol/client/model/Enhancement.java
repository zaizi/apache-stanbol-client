package org.zaizi.stanbol.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents an enhancement
 * 
 * @author efoncubierta
 *
 */
public abstract class Enhancement {	
	
	protected Resource resource;
	
	// properties
	private Date created;                // http://purl.org/dc/terms/created
	private String creator;              // http://purl.org/dc/terms/creator
	private List<Enhancement> relation;  // http://purl.org/dc/terms/relation
	
	// proposed properties
//	private String contributor;
//	private Date modified;
//	private List<Enhancement> requires;
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public Enhancement(Resource resource) {
		this.resource = resource;
		
//		this.modified = new Date();
//		this.contributor = "";
//		this.requires = new ArrayList<Enhancement>();
	}
	
	/**
	 * Get the dc:creator property
	 * 
	 * @return dc:creator property
	 */
	public String getCreator() {
		if(creator == null && resource.hasProperty(DCTerms.creator)) {
			creator = resource.getProperty(DCTerms.creator).getString();
		}
		return creator;
	}
	
	/**
	 * Get the dc:created property
	 * 
	 * @return dc:created property
	 */
	public Date getCreated() {
		if(created == null && resource.hasProperty(DCTerms.created)) {
			// TODO extract data
			created = new Date();
		}
		return created;
	}
	
	/**
	 * Get the dc:relation property
	 * 
	 * @return dc:relation property
	 */
	public List<Enhancement> getRelation() {
		if(relation == null && resource.hasProperty(DCTerms.relation)) {
			relation = new ArrayList<Enhancement>();
			
			final StmtIterator relationsIterator = resource.listProperties(DCTerms.relation);
			while(relationsIterator.hasNext()) {
				final Statement relationStatement = relationsIterator.next();
				
				relation.add(EnhancementParser.parse(relationStatement.getObject().asResource()));
			}
		}
		return relation;
	}

//	public String getContributor() {
//		return contributor;
//	}
	
//	public Date getModified() {
//		return modified;
//	}
	
//	public List<Enhancement> getRequires() {
//		return requires;
//	}
}
