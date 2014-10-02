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
package org.apache.stanbol.client.enhancer.model;

import java.util.Collection;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents an enhancement in the FISE ontology
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 * 
 */
public abstract class Enhancement
{

    /**
     * Jena Resource
     */
    Resource resource;

    // properties
    private final String uri;
    private final String created; // http://purl.org/dc/terms/created
    private final String creator; // http://purl.org/dc/terms/creator
    private Collection<Enhancement> relation; // http://purl.org/dc/terms/relation

    /**
     * Constructor
     * 
     * @param resource Jena resource modeling the RDF Enhancement
     */
    protected Enhancement(Resource resource)
    {
        this.resource = resource;
        this.uri = resource.getURI();
        this.created = resource.hasProperty(DCTerms.created) ? resource.getProperty(DCTerms.created).getString() : null;
        this.creator = resource.hasProperty(DCTerms.creator) ? resource.getProperty(DCTerms.creator).getString() : null;
        this.relation = Sets.newHashSet();
        
//        if (resource.hasProperty(DCTerms.relation))
//        {
//            final StmtIterator relationsIterator = resource.listProperties(DCTerms.relation);
//            while (relationsIterator.hasNext())
//            {
//                final Statement relationStatement = relationsIterator.next();
//                this.relation.add(EnhancementParser.parse(relationStatement.getObject().asResource()));
//            }
//        }
    }
    
    void addRelation(Enhancement e){
    	this.relation.add(e);
    }
    
    void setRelations(Collection<Enhancement> relations){
    	this.relation = relations;
    }

    /**
     * Get resource URI
     * 
     * @return Resource URI
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * Get the dc:creator property
     * 
     * @return dc:creator property
     */
    public String getCreator()
    {
        return creator;
    }

    /**
     * Get the dc:created property
     * 
     * @return dc:created property
     */
    public String getCreated()
    {
        return created;
    }

    /**
     * Get the dc:relation property
     * 
     * @return dc:relation property
     */
    public Collection<Enhancement> getRelation()
    {
        return relation;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Enhancement other = (Enhancement) obj;
        if (uri == null)
        {
            if (other.uri != null)
                return false;
        }
        else if (!uri.equals(other.uri))
            return false;
        return true;
    }
    
    
}
