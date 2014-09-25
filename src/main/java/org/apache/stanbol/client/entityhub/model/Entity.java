/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.stanbol.client.entityhub.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.collect.Maps;

import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Represent an Entity from the EntityHub
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 * 
 */
public class Entity
{

    private String uri;
    private Resource resource;
    private String site;

    /**
     * Constructor
     * 
     * @param model Jena Model containing an RDF representation of the Entity
     * @param uri Entity's URI
     */
    public Entity(Model model, String uri)
    {
        this.resource = model.getResource(uri);
        this.uri = uri;

        if (model.getProperty(model.getResource(uri + ".meta"),
                model.createProperty("http://stanbol.apache.org/ontology/entityhub/entityhub#", "site")) != null)
                    this.site = model.getResource(uri + ".meta").getProperty(
                                model.createProperty("http://stanbol.apache.org/ontology/entityhub/entityhub#", "site")).getString();
        else
            this.site = "queryResult";
    }
    
    public Entity(Resource resource, String site){
    	this.resource = resource;
    	this.uri = resource.getURI();
    	this.site = site;
    }

    /**
     * Get Entity URI
     * 
     * @return Resource URI
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * Get Entity's Labels
     * 
     * @return All labels for the entity
     */
    public Collection<String> getLabels()
    {

        List<String> result = new ArrayList<String>();
        NodeIterator iterator = resource.getModel().listObjectsOfProperty(RDFS.label);

        while (iterator.hasNext())
            result.add(iterator.next().asLiteral().getString());

        return result;
    }

    /**
     * Get Entity's Labels by Language
     * 
     * @param language
     * @return All labels for the entity in the passed language
     */
    public Collection<String> getLabels(String language)
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(RDFS.label);

        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            if (nextSt.getLiteral().getLanguage().equals(language))
                result.add(nextSt.getString());
        }

        return result;
    }
    
    /**
     * Get a Map of language code and associated label in that language
     * 
     * @return
     */
    public Map<String, String> getLabelsByLanguage(){
    	Map<String, String> labels = Maps.newHashMap();
    	StmtIterator iterator = resource.listProperties(RDFS.label);

        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            labels.put(nextSt.getLanguage(), nextSt.getString());
        }

        return labels;
    }

    /**
     * Get Entity's Categories
     * 
     * @return All categories for the entity
     */
    public Collection<String> getCategories()
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(DCTerms.subject);

        while (iterator.hasNext())
        	result.add(iterator.next().getObject().toString());

        return result;
    }

    /**
     * Get Entity's Types
     * 
     * @return All types for the entity
     */
    public Collection<String> getTypes()
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(RDF.type);

        while (iterator.hasNext())
        {
            Statement nextNode = iterator.next();
            String namespace = nextNode.getObject().asResource().getNameSpace();
            if (!namespace.equals("http://www.w3.org/2002/07/owl#"))
            {
                String literal = nextNode.getObject().toString();
                result.add(literal);
            }
        }

        return result;
    }

    /**
     * Get Entity Property Value as a String. For Resource properties, Resource LocalName will be extracted
     * 
     * @param namespace Property Namespace
     * @param propertyName Property Name
     * @return List of values for the Property
     */
    public Collection<String> getPropertyValues(String namespace, String propertyName)
    {
        return getPropertyValues(namespace, propertyName, true);
    }
    
    /**
     * Get the List of Entity Property's Values as Strings
     * 
     * @param property Property's URI
     * @return List of literal values for the property
     */
    public Collection<String> getPropertyValues(String property){
    	Property p = resource.getModel().createProperty(property);
    	return getPropertyStringValues(p);
    }
    
    /**
     * Return all Literal values of the property for the specified language
     * 
     * @param property Property's URI
     * @param language Language
     * @return 
     */
    public Collection<String> getPropertyValuesByLanguage(String property, String language){
    	Property p = resource.getModel().createProperty(property);
    	return getPropertyValuesByLanguage(p, language);
    }
    
    /**
     * Return all Literal values of the property for the specified language
     * 
     * @param property {@link Property}
     * @param language Language
     * @return
     */
    public Collection<String> getPropertyValuesByLanguage(Property property, String language){
    	List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(property);
        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            if (nextSt.getLiteral().getLanguage().equals(language))
                result.add(nextSt.getString());
        }
        return result;
    }
    
    /**
     * Returns a Map of Language Code-Property value 
     * 
     * @param property Property's URI
     * @return
     */
    public Map<String, String> getPropertyValuesByLanguage(String property){
    	Property p = resource.getModel().createProperty(property);
    	return getPropertyValuesByLanguage(p);
    }
    
    /**
     * Returns a Map of Language Code-Property value
     * 
     * @param property {@link Property}
     * @return
     */
    public Map<String, String> getPropertyValuesByLanguage(Property property) {
		Map<String, String> result = Maps.newHashMap();
		StmtIterator iterator = resource.listProperties(property);
        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            RDFNode object = nextSt.getObject();
            if(object.isLiteral() &&
            		object.asLiteral().getLanguage() != null)
            	result.put(object.asLiteral().getLanguage(), object.asLiteral().getString());
            	
        }
		return result;
	}

	/**
     * Get the List of Entity's Property Values
     * 
     * @param property {@link Property}
     * @return List of literal values for the Property
     */
    public Collection<RDFNode> getPropertyValues(Property property)
    {
        List<RDFNode> result = new ArrayList<RDFNode>();
        StmtIterator iterator = resource.listProperties(property);
        while(iterator.hasNext()){
            Statement next = iterator.next();
            result.add(next.getObject());
        }
        return result;
    }
    
    /**
     * Get the List of Entity Property's Values as Strings
     * 
     * @param property Property
     * @return List of values for the Property
     */
    public Collection<String> getPropertyStringValues(Property property)
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(property);
        while(iterator.hasNext()){
            Statement next = iterator.next();
            if(next.getObject().isLiteral())
                result.add(next.getObject().asLiteral().getString());
            else
                result.add(next.getObject().asResource().getLocalName());
        }
        return result;
    }

    /**
     * Get Entity Property Value as a String. For Resource properties, if getLocalName is set to true, Resource
     * LocalName will be extracted, in other case Resource URI will be returned as property value
     * 
     * @param namespace Property Namespace
     * @param propertyName Property Name
     * @param getLocalName Resources' LocalName extraction flag
     * @return List of values for the Entity Property
     */
    public Collection<String> getPropertyValues(String namespace, String propertyName, boolean getLocalName)
    {

        List<String> result = new ArrayList<String>();
        NodeIterator iterator = resource.getModel().listObjectsOfProperty(resource,
                resource.getModel().createProperty(namespace, propertyName));

        while (iterator.hasNext())
        {
            RDFNode rdfNode = iterator.next();
            try
            {
                result.add(rdfNode.asLiteral().getString());
            }
            catch (LiteralRequiredException e) // Custom Namespaces
            {
                result.add(getLocalName ? rdfNode.asResource().getLocalName() : rdfNode.asResource().getURI());
            }
        }

        return result;
    }

    /**
     * Set Entity Property Value
     * 
     * @param namespace Property's namespace
     * @param propertyName Property's name
     * @param value Property's value
     */
    public void setProperty(String namespace, String propertyName, String value)
    {
        Property p = resource.getModel().createProperty(namespace, propertyName);
        resource.addProperty(p, value);
    }

    /**
     * Get Entity's Properties' URIs
     * 
     * @return List of Properties' URIs
     */
    public Collection<String> getProperties()
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties();
        while (iterator.hasNext())
            result.add(iterator.next().getPredicate().getURI());
        return result;
    }

    /**
     * Get Entity Descriptions by language
     * 
     * @param language
     * @return Entity's Descriptions in the passed language
     */
    public Collection<String> getComments(String language)
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(RDFS.comment);

        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            if (nextSt.getLiteral().getLanguage().equals(language))
                result.add(nextSt.getString());
        }

        return result;
    }
    
    /**
     * Get Entity's Descriptions in all languages
     * 
     * @return
     */
    public Collection<String> getComments(){
    	List<String> result = new ArrayList<String>();
    	StmtIterator iterator = resource.listProperties(RDFS.comment);
        while (iterator.hasNext())
        	result.add(iterator.next().getLiteral().getString());
    	return result;
    }
    
    /**
     * Get a Map of language code and associated description in that language
     * 
     * @return
     */
    public Map<String, String> getCommentsByLanguage(){
    	Map<String, String> comments = Maps.newHashMap();
    	StmtIterator iterator = resource.listProperties(RDFS.comment);

        while (iterator.hasNext())
        {
            Statement nextSt = iterator.next();
            comments.put(nextSt.getLanguage(), nextSt.getString());
        }

        return comments;
    }

    /**
     * Get Entity's ReferencedSite
     * 
     * @return Referenced Site Name
     */
    public String getReferencedSite()
    {
        return site;
    }
    
    /**
     * Get Entity RDF representation
     * 
     * @return RDF Resource associated to the entity
     */
    public Resource getResource()
    {
        return resource;
    }

    /**
     * Get Entity RDF/XML Text Representation
     * 
     * @return InputStream in XML format
     */
    public InputStream getStream(){
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();
        resource.getModel().write(buffer, "RDF/XML");
        InputStream is= new ByteArrayInputStream(buffer.toByteArray());
        return is;
    }
}
