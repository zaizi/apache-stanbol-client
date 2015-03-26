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
package org.apache.stanbol.client;

import java.io.InputStream;
import java.util.Collection;

import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Define operations for Stanbol Entityhub
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 * 
 */
public interface EntityHub
{

    // Stanbol Enhancer service url
    public static final String STANBOL_ENTITYHUB_PATH = "entityhub";
    public static final String STANBOL_ENTITYHUB_SITE_PATH =  "site";
    public static final String STANBOL_ENTITYHUB_SITEMANAGER_PATH = "sites";

    /**
     * This service returns a list containing the IDs of all referenced sites configured in Stanbol
     * 
     * @return List of sites URLs
     * @throws StanbolServiceException
     */
    public Collection<String> getReferencedSites() throws StanbolServiceException;

    /**
     * Get an entity managed by the EntityHub Cache
     * 
     * @param id Entity's URI
     * @return Entity {@link Entity}
     * @throws StanbolServiceException
     */
    public Entity get(String id) throws StanbolServiceException;

    /**
     * This service searches the referenced site passed by parameter for the entity with the passed URI. 
     * If the site parameter is null or doesn't exist as ReferencedSite, the service would search the entity over 
     * all referenced sites configured in Stanbol. If the requested entity can not be found a null object is returned.
     * 
     * @param site Referenced Site to search. If null or not exist, all referenced sites will be used
     * @param id Entity's URI
     * @return
     * @throws StanbolServiceException
     */
    public Entity get(String site, String id) throws StanbolServiceException;

    /**
     * Create entities in the EntityHub. If any of such Entities already exists within the Entityhub and the update parameter
     * is false, a {@link StanbolServiceException} will be thrown
     * 
     * @param is InputStream containing entities' RDF data
     * @param id URI of the entity. If the URI is null, RDF entities' ids will be used. If the URI is not null, only the
     *            referenced entity will be created or updated
     * @param update If true, entities that already exist will be updated
     * @return URI of the created Entity.
     * @throws StanbolServiceException
     */
    public String create(InputStream is, String id, Boolean update) throws StanbolServiceException;
    
    /**
     * Create an entity in the EntityHub. If the Entity already exists within the Entityhub and the update parameter
     * is false, a {@link StanbolServiceException} will be thrown
     * 
     * @param entity Entity to be created
     * @param update If true and the Entity already exists within the EntityHub, the Entity will be updated
     * @return URI of the created Entity
     * @throws StanbolServiceException
     */
    public String create(Entity entity, Boolean update) throws StanbolServiceException;

    /**
     * Update entities for the EntityHub. If any of such Entities doesn't exist within the Entityhub and create parameter is
     * false, a {@link StanbolServiceException} will be thrown
     * 
     * @param is InputStream containing entities' RDF data
     * @param id URI of the entity. If the URI is null, RDF entities' ids will be used. If the URI is not null, only the
     *            referenced entity will be created or updated
     * @param create If true, entities that don't exist will be created
     * @return Data of the entity
     * @throws StanbolServiceException
     */
    public Entity update(InputStream is, String id, Boolean create) throws StanbolServiceException;
       
    /**
     * Update entities for the EntityHub. If any of such Entities doesn't exist within the Entityhub and create parameter is
     * false, a {@link StanbolServiceException} will be thrown
     * 
     * @param entity Entity to be updated
     * @return Data of the entity
     * @throws StanbolServiceException
     */
    public Entity update(Entity entity, Boolean update) throws StanbolServiceException;

    /**
     * Delete an entity managed by the Entityhub by its URI
     * 
     * @param id URI of the Entity to delete
     * @return boolean True is the Entity has been successfully deleted
     * @throws StanbolServiceException
     */
    public Boolean delete(String id) throws StanbolServiceException;

    /**
     * Delete all entities managed by the Entityhub
     * 
     * @return boolean
     * @throws StanbolServiceException
     */
    public Boolean deleteAll() throws StanbolServiceException;

    /**
     * This service looks-up Symbols (Entities managed by the Entityhub) based on the passed URI. The passed ID can be
     * the URI of a Symbol or an Entity of any referenced site.
     * 
     * If the passed ID is a String of a Symbol, then the stored information of the Symbol are returned If the passed ID is
     * an URI of an already mapped entity, then the existing mapping is used to get the according Symbol. If "create" is
     * enabled, and the passed URI is not already mapped to a Symbol, than all the currently active referenced sites are
     * searched for an Entity with the passed URI. If the configuration of the referenced site allows to create new
     * symbols, then the entity is imported in the Entityhub, a new Symbol and EntityMapping is created and the newly
     * created Symbol is returned. In case the entity is not found (this also includes if the entity would be available
     * via a referenced site, but create=false) a null object is returned. In case the entity is found on a referenced
     * site, but the creation of a new Symbol is not allowed a {@link StanbolServiceException} is thrown
     * 
     * @param id URI of the Entity/Symbol/ReferencedSite
     * @param create If true, a new symbol is created if necessary and allowed
     * @return
     */
    public Entity lookup(String id, Boolean create) throws StanbolServiceException;

    /**
     * Find locally managed Entities by label based search
     * 
     * @param name The name of the Entity to search. Supports '*' and '?
     * @param field The name of the field to search the name. Optional, default is rdfs:label
     * @param language The language of the parsed name (default: any)
     * @param ldpath The LDPath program executed for entities selected by the find query (optionally). The LDPath
     *            program needs to be URLEncoded.
     * @param limit The maximum number of returned Entities (optional)
     * @param offset The offset of the first returned Entity (default: 0)
     * @return List of finded entities
     * @throws StanbolServiceException
     */
    public Collection<Entity> search(String name, String field, String language, LDPathProgram ldpath, int limit, int offset)
            throws StanbolServiceException;

    /**
     * Find ReferencedSite managed Entities by label based search
     * 
     * @param site Referenced Site to search. If null or not exist, all referenced sites will be used
     * @param name The name of the Entity to search. Supports '*' and '?
     * @param field The name of the field to search the name. Optional, default is rdfs:label
     * @param language The language of the parsed name (default: any)
     * @param ldpath The LDPath program executed for entities selected by the find query (optionally). The LDPath
     *            program needs to be URLEncoded.
     * @param limit The maximum number of returned Entities (optional)
     * @param offset The offset of the first returned Entity (default: 0)
     * @return List of finded entities
     * @throws StanbolServiceException
     */
    public Collection<Entity> search(String site, String name, String field, String language, LDPathProgram ldpath,
            int limit, int offset) throws StanbolServiceException;

    /**
     * Allows to execute an LDPath program on one or more Entities (contexts)
     * 
     * @param contexts The list of entities' URIs used as context for the execution of the LDPath program
     * @param ldPathProgram The LDPath program to execute
     * @return An RDF Graph with the passed context(s) as subject the field selected by the LDPath program as properties
     *         and the selected values as object.
     * @throws StanbolServiceException
     */
    public Model ldpath(String contexts, LDPathProgram ldPathProgram) throws StanbolServiceException;

    /**
     * Allows to execute an LDPath program on one or more Entities (contexts)
     * 
     * @param site Referenced Site to execute the ldpath. If null or not exist, all referenced sites will be used
     * @param contexts The list of entities' URIs used as context for the execution of the LDPath program
     * @param ldPathProgram The LDPath program to execute
     * @return An RDF Graph with the passed context(s) as subject the field selected by the LDPath program as properties
     *         and the selected values as object.
     * @throws StanbolServiceException
     */
    public Model ldpath(String site, String contexts, LDPathProgram ldPathProgram) throws StanbolServiceException;

}
