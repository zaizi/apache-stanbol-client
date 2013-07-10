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

import java.util.ArrayList;
import java.util.List;

import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.services.StanbolEntityhubService;
import org.apache.stanbol.client.ontology.FISE;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Represents an entity annotation in the FISE ontology
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class EntityAnnotation extends Annotation
{

    // properties
    private final String entityLabel; // http://fise.iks-project.eu/ontology/entity-label
    private final String entityReference; // http://fise.iks-project.eu/ontology/entity-reference
    private final List<String> entityTypes; // http://fise.iks-project.eu/ontology/entity-type
    private final String site; // http://fise.iks-project.eu/ontology/entity-site
    

    /**
     * Constructor
     * 
     * @param resource Jena resource
     */
    public EntityAnnotation(Resource resource)
    {
        super(resource);
        this.entityLabel = resource.hasProperty(FISE.ENTITY_LABEL) ? resource.getProperty(FISE.ENTITY_LABEL).getString() : null;
        this.entityReference = resource.hasProperty(FISE.ENTITY_REFERENCE) ? resource.getPropertyResourceValue(FISE.ENTITY_REFERENCE).getURI() : null;
        this.site = resource.hasProperty(FISE.ENTITYHUB_SITE) ? resource.getProperty(FISE.ENTITYHUB_SITE).getString() : null;
        
        if(resource.hasProperty(FISE.ENTITY_TYPE)){
            entityTypes = new ArrayList<String>();
            StmtIterator iterator = resource.listProperties(FISE.ENTITY_TYPE);
            while(iterator.hasNext())
                entityTypes.add(iterator.next().getObject().asResource().getURI());
        }
        else
            entityTypes = null;
    }

    /**
     * Get the fise:entity-label property
     * 
     * @return fise:entity-label property
     */
    public String getEntityLabel()
    {
        return entityLabel;
    }

    /**
     * Get the fise:entity-reference property
     * 
     * @return fise:entity-reference property
     */
    public String getEntityReference()
    {
        return entityReference;
    }

    /**
     * Get the fise:entity-type property
     * 
     * @return fise:entity-type property
     */
    public List<String> getEntityTypes()
    {
        return entityTypes;
    }
    
    /**
     * Get the fise:site property
     * 
     * @return fise:site property
     */
    public String getSite()
    {
        return site;
    }
    
    /**
     * Retrieve the Entity Associated to the EntityAnnotation from Stanbol Local EntityHub 
     * 
     * @param service {@link StanbolEntityhubService}
     * @return {@link Entity} object
     */
    public Entity getEntity(StanbolEntityhubService service){
        try
        {
            return service.get(this.entityReference);
        }
        catch (StanbolServiceException e)
        {
            return null;
        }
    }
    
    /**
     * Retrieve the Entity Associated to the EntityAnnotation from Stanbol EntityHub Referenced site
     * 
     * @param site Referenced Site within EntityHUb
     * @param service {@link StanbolEntityhubService}
     * @return {@link Entity} object
     */
    public Entity getEntity(String site, StanbolEntityhubService service){
        try
        {
            return service.get(site, this.entityReference);
        }
        catch (StanbolServiceException e)
        {
            return null;
        }
    }
}
