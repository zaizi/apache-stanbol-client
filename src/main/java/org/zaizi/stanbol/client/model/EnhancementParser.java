package org.zaizi.stanbol.client.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extract enhancements objects from several kind of objects 
 * 
 * @author efoncubierta
 *
 */
public class EnhancementParser {
	
	/**
	 * Parse a Jena model as a list of enhancements
	 * 
	 * @param model Jena model
	 * @return List of enhancements
	 */
	public static List<Enhancement> parse(Model model) {
		List<Enhancement> enhancements = new ArrayList<Enhancement>();
		
		final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type);
		while(enhancementsIterator.hasNext()) {
			final Resource enhancementResource = enhancementsIterator.next();
			final Enhancement enhancement = parse(enhancementResource);
			
			if(enhancement != null) {
				enhancements.add(enhancement);
			}
		}
		return enhancements;
	}
	
	/**
	 * Parse a Jena resource as an enhancement
	 * 
	 * @param resource Jena resource
	 * @return Enhancement
	 */
	public static Enhancement parse(Resource resource) {
		Enhancement enhancement = null;
		
		if(resource != null) {
			final StmtIterator types = resource.listProperties(RDF.type);
			while(types.hasNext() && enhancement == null) {
				final Statement stmt = types.next();
				final String name = stmt.getObject().asResource().getURI();
				if("http://fise.iks-project.eu/ontology/TextAnnotation".equals(name)) {
					enhancement = new TextAnnotation(resource);
				} else if("http://fise.iks-project.eu/ontology/EntityAnnotation".equals(name)) {
					enhancement = new EntityAnnotation(resource);
				}
			}
		}
		
		return enhancement;
	}
}
