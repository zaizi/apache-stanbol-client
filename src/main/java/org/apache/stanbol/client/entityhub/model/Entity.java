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
import java.util.Iterator;
import java.util.List;

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
 * @author Rafa Haro
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
    public List<String> getLabels()
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
    public List<String> getLabels(String language)
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
     * Get Entity's Categories
     * 
     * @return All categories for the entity
     */
    public List<String> getCategories()
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(DCTerms.subject);

        while (iterator.hasNext())
        {
            Statement nextNode = iterator.next();
            String namespace = nextNode.getObject().asResource().getNameSpace();
            String literal = nextNode.getObject().toString();
            String normalizedNamespace = namespace.substring(0, namespace.lastIndexOf(':') + 1);
            String normalizedLiteral = literal.replace(normalizedNamespace, "").replace("_", " ");
            result.add(normalizedLiteral);

        }

        return result;
    }

    /**
     * Get Entity's Types
     * 
     * @return All types for the entity
     */
    public List<String> getTypes()
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
                String normalizedLiteral = this.uncaps(literal.replace(namespace, ""));
                result.add(normalizedLiteral);
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
    public List<String> getPropertyValue(String namespace, String propertyName)
    {
        return getPropertyValue(namespace, propertyName, true);
    }
    
    /**
     * Get the List of Entity's Property Values
     * 
     * @param property Property
     * @return List of values for the Property
     */
    public List<RDFNode> getPropertyValues(Property property)
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
    public List<String> getPropertyStringValues(Property property)
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
    public List<String> getPropertyValue(String namespace, String propertyName, boolean getLocalName)
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
    public List<String> getProperties()
    {
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties();
        while (iterator.hasNext())
            result.add(iterator.next().getPredicate().getURI());
        return result;
    }

    /**
     * Get Entity Description by language
     * 
     * @return Entity Comment
     */
    public List<String> getComment(String language)
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

    /*** Aux Private Methods ***/
    private String uncaps(String string)
    {
        String result = "";
        char[] charArray = string.toCharArray();
        for (int i = 1; i < charArray.length; i++)
        {
            List<Character> nextStr = new ArrayList<Character>();
            nextStr.add(charArray[i - 1]);

            if (Character.isUpperCase(charArray[i]))
            {
                for (; i < charArray.length && Character.isUpperCase(charArray[i]); i++)
                    nextStr.add(charArray[i]);

                i--;
                nextStr.remove(nextStr.size() - 1);
            }

            if (Character.isLowerCase(charArray[i]))
                for (; i < charArray.length
                        && (charArray[i] == '_' || charArray[i] == '-' || Character.isLowerCase(charArray[i])); i++)
                    nextStr.add(charArray[i]);

            char[] nextStrArray = new char[nextStr.size()];
            Iterator<Character> it = nextStr.iterator();
            int j = 0;
            while (it.hasNext())
            {
                nextStrArray[j] = it.next();
                j++;
            }

            result += new String(nextStrArray) + " ";

        }

        return result.substring(0, result.length() - 1);
    }
}
