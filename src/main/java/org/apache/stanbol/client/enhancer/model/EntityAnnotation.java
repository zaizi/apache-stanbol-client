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

import org.apache.stanbol.client.EntityHub;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Represents an entity annotation in the FISE ontology
 * 
 * @author efoncubierta
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 * 
 */
public class EntityAnnotation extends Annotation
{

    // properties
    private final String entityLabel; // http://fise.iks-project.eu/ontology/entity-label
    private final String entityReference; // http://fise.iks-project.eu/ontology/entity-reference
    private final List<String> entityTypes; // http://fise.iks-project.eu/ontology/entity-type
    private final String site; // http://fise.iks-project.eu/ontology/entity-site
    private final Entity entity;
    

    /**
     * Constructor
     * 
     * @param resource Jena resource
     */
    EntityAnnotation(Resource resource, 
    		Entity dereferencedEntity)
    {
        super(resource);
        this.entityLabel = resource.hasProperty(EnhancementStructureOntology.ENTITY_LABEL) ? resource.getProperty(EnhancementStructureOntology.ENTITY_LABEL).getString() : null;
        this.entityReference = resource.hasProperty(EnhancementStructureOntology.ENTITY_REFERENCE) ? resource.getPropertyResourceValue(EnhancementStructureOntology.ENTITY_REFERENCE).getURI() : null;
        this.site = resource.hasProperty(EnhancementStructureOntology.ENTITYHUB_SITE) ? resource.getProperty(EnhancementStructureOntology.ENTITYHUB_SITE).getString() : null;
        
        if(resource.hasProperty(EnhancementStructureOntology.ENTITY_TYPE)){
            entityTypes = new ArrayList<String>();
            StmtIterator iterator = resource.listProperties(EnhancementStructureOntology.ENTITY_TYPE);
            while(iterator.hasNext())
                entityTypes.add(iterator.next().getObject().asResource().getURI());
        }
        else
            entityTypes = null;
        
        entity = dereferencedEntity;
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
    
    public Entity getDereferencedEntity(){
    	return entity;
    }
    
    /**
     * Retrieve the Entity Associated to the EntityAnnotation from Stanbol Local EntityHub 
     * 
     * @param service {@link EntityHub}
     * @return {@link Entity} object
     */
    public Entity getEntity(EntityHub service){
        try
        {
            return service.get(this.entityReference);
        }
        catch (StanbolServiceException e)
        {
            return null;
        } catch (StanbolClientException e) {
			return null;
		}
    }
    
    /**
     * Retrieve the Entity Associated to the EntityAnnotation from Stanbol EntityHub Referenced site
     * 
     * @param site Referenced Site within EntityHUb
     * @param service {@link EntityHub}
     * @return {@link Entity} object
     */
    public Entity getEntity(String site, EntityHub service){
        try
        {
            return service.get(site, this.entityReference);
        }
        catch (StanbolServiceException e)
        {
            return null;
        } catch (StanbolClientException e) {
			return null;
		}
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EntityAnnotation [getEntityLabel()=");
		builder.append(getEntityLabel());
		builder.append(", getEntityReference()=");
		builder.append(getEntityReference());
		builder.append(", getEntityTypes()=");
		builder.append(getEntityTypes());
		builder.append(", getSite()=");
		builder.append(getSite());
		builder.append(", getDereferencedEntity()=");
		builder.append(getDereferencedEntity());
		builder.append(", getExtractedFrom()=");
		builder.append(getExtractedFrom());
		builder.append(", getConfidence()=");
		builder.append(getConfidence());
		builder.append(", getUri()=");
		builder.append(getUri());
		builder.append(", getCreator()=");
		builder.append(getCreator());
		builder.append(", getCreated()=");
		builder.append(getCreated());
		builder.append(", getRelation()=");
		builder.append(getRelation());
		builder.append("]");
		return builder.toString();
	}
}
