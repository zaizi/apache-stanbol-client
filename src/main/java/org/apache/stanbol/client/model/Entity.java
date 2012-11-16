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

package org.apache.stanbol.client.model;

 import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Represent an Entity from the entityhub
 * 
 * @author rharo
 * 
 */
public class Entity {

    private String uri;
	private Resource resource;
	private String site;
	
	
	/**
	 * 
	 * @param model
	 * @param uri
	 */
	public Entity(Model model, String uri) {
	    this.resource = model.getResource(uri);
		this.uri = uri;
						
		if(model.getProperty(model.getResource(uri+".meta"), model.createProperty("http://stanbol.apache.org/ontology/entityhub/entityhub#", "site")) != null)
		    this.site = model.getResource(uri+".meta").getProperty(model.createProperty("http://stanbol.apache.org/ontology/entityhub/entityhub#", "site")).getString();
		 else
		    this.site = "queryResult";
		 
	}	
    /**
     * Get resource URI
     * 
     * @return Resource URI
     */
    public String getUri() {
        return uri;
    }
        	
    /**
     * Get Entity's Labels
     * 
     * @return All labels for the entity 
     */
    public List<String> getLabels(){
       
       List<String> result = new ArrayList<String>();
       NodeIterator iterator = resource.getModel().listObjectsOfProperty(RDFS.label);
       
       while(iterator.hasNext())
           result.add(iterator.next().asLiteral().getString());
       
        return result;
    }
    
    /**
     * Get Entity's Labels by Language
     * 
     * @param language
     * @return All labels for the entity in the passed language
     */
    public List<String> getLabels(String language){
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(RDFS.label);
        
        while(iterator.hasNext()){
            Statement nextSt = iterator.next();
            if(nextSt.getLiteral().getLanguage().equals(language))
                result.add(nextSt.getString());
        }
        
        return result;
    }

    /**
     * Get Entity's Categories
     * 
     * @return All categories for the entity
     */
    public List<String> getCategories(){
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(resource.getModel().createProperty("http://purl.org/dc/terms/", "subject"));
      
        while(iterator.hasNext()){
            Statement nextNode = iterator.next();
            String namespace = nextNode.getObject().asResource().getNameSpace();
            String literal = nextNode.getObject().toString();
            String normalizedNamespace = namespace.substring(0, namespace.lastIndexOf(':')+1);            
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
    public List<String> getTypes(){
        List<String> result = new ArrayList<String>();
        StmtIterator iterator = resource.listProperties(RDF.type);
        
        while(iterator.hasNext()){
            Statement nextNode = iterator.next();
            String namespace = nextNode.getObject().asResource().getNameSpace();
            if(!namespace.equals("http://www.w3.org/2002/07/owl#")){
                String literal = nextNode.getObject().toString();
                String normalizedLiteral = this.uncaps(literal.replace(namespace, ""));
                result.add(normalizedLiteral);
            }
        }
        
         return result;
    }
    
    /**
     * Get Object's values for the passed property
     * 
     * @param property 
     * @return String value for the object of the property
     */
    public List<String> getPropertyValue(String namespace, String propertyName){
        List<String> result = new ArrayList<String>();
        NodeIterator iterator = resource.getModel().listObjectsOfProperty(resource, resource.getModel().createProperty(namespace, propertyName));
        
        while(iterator.hasNext())
            result.add(iterator.next().asLiteral().getString());
        
         return result;
    }
    
    public void setProperty(String namespace, String propertyName, String value){
        Property p = resource.getModel().createProperty(namespace, propertyName);
        resource.addProperty(p, value);
    }
    
    /**
     * 
     * @return
     */
    public List<String> getProperties(){
       List<String> result = new ArrayList<String>();
       StmtIterator iterator = resource.listProperties();
       while(iterator.hasNext())
           result.add(iterator.next().asTriple().getPredicate().getURI());
       return result;
    }
    
    /**
     * Get Entity Brief Text Description
     * 
     * @return Entity Comment
     */
    public String getComment(){
        return resource.getProperty(RDFS.comment).getObject().asLiteral().getString();
    }
    
    /**
     * Get Entity's ReferencedSite
     * 
     * @return
     */
    public String getReferencedSite(){
        return site;
    }
    
    /*** Aux Private Methods ***/
    private String uncaps(String string)
    {
        String result = "";
        char[] charArray = string.toCharArray();
        for(int i = 1; i < charArray.length; i++){
            List<Character> nextStr = new ArrayList<Character>();
            nextStr.add(charArray[i-1]);
            
            if(Character.isUpperCase(charArray[i])){
                for(; i < charArray.length && Character.isUpperCase(charArray[i]); i++)
                    nextStr.add(charArray[i]);
                
                i--;
                nextStr.remove(nextStr.size()-1);
            }
            
            if(Character.isLowerCase(charArray[i]))
                for(; i < charArray.length && (charArray[i] == '_' || charArray[i] == '-' || Character.isLowerCase(charArray[i])); i++)
                    nextStr.add(charArray[i]);
            
            char[] nextStrArray = new char[nextStr.size()];
            Iterator<Character> it = nextStr.iterator();
            int j = 0;
            while(it.hasNext()){
                nextStrArray[j] = it.next();
                j++;
            }
            
            result += new String(nextStrArray) + " ";
                                  
        }
                               
        return result.substring(0, result.length()-1);
    }
}
