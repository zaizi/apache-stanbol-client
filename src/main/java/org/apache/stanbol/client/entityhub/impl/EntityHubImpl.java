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
package org.apache.stanbol.client.entityhub.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.stanbol.client.EntityHub;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.rest.RestClientExecutor;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * EntityHub Service Client Implementation
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 *
 */
public class EntityHubImpl implements EntityHub
{

	private Logger logger = LoggerFactory.getLogger(EntityHubImpl.class);

	private UriBuilder builder;

	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public EntityHubImpl(UriBuilder builder)
	{
		this.builder = builder;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#getReferencedSites()
	 */
	@Override
	public Collection<String> getReferencedSites() throws StanbolServiceException
	{
		UriBuilder clientBuilder = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path(STANBOL_ENTITYHUB_SITEMANAGER_PATH).
				path("referenced");

		URI uri = clientBuilder.build();
		JSONArray array = null;
		try{
			array = RestClientExecutor.get(uri, 
					new MediaType("application", "rdf+xml"),
					Collections.<String, String> emptyMap(),
					JSONArray.class);

			if (logger.isDebugEnabled())
			{
				logger.debug("Stanbol Sites sucessfully retrieved");
			}
		} catch(UniformInterfaceException e){
			ClientResponse response = e.getResponse();  

			// Check HTTP status code
			int status = response.getStatus();
			if (status != 200 && status != 201 && status != 202)
			{
				if(logger.isTraceEnabled()){
					String stackTrace = response.getEntity(String.class);
					logger.trace(stackTrace);
				}
				throw new StanbolServiceException("[HTTP " + status + "] Error retrieving sites from stanbol server");
			}

		} catch(ClientHandlerException e){
			String message = "Error parsing Response";
			if(logger.isTraceEnabled())
				logger.trace(message, e);
			throw new StanbolServiceException(message);
		}

		List<String> result = Lists.newArrayList();
		for (int i = 0; i < array.length(); i++)
			try
		{
				result.add(array.getString(i));
		}
		catch (JSONException e)
		{
			String message = "Malformed JSON response for EntityHub referenced service";
			logger.error(message);
			throw new StanbolServiceException(message);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#get(java.lang.String)
	 */
	@Override
	public Entity get(String id) throws StanbolServiceException
	{
		URI uri = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path("entity").
				queryParam("id", id).
				build();
		return getAux(uri, id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#get(java.lang.String, java.lang.String)
	 */
	@Override
	public Entity get(String site, String id) throws StanbolServiceException
	{
		URI uri = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path(STANBOL_ENTITYHUB_SITE_PATH).
				path(site).
				path("entity").
				queryParam("id", id).
				build();
		return getAux(uri, id);
	}

	private Entity getAux(URI uri, String id) throws StanbolServiceException
	{


		try{
			InputStream response = RestClientExecutor.get(uri, 
					new MediaType("application", "rdf+xml"),
					Collections.<String, String> emptyMap(),
					InputStream.class);
			if (logger.isDebugEnabled())
			{
				logger.debug("Content " + id + " has been sucessfully loaded");
			}

			return parse(id, response, false);
		}catch(UniformInterfaceException e){

			ClientResponse response = e.getResponse();
			int status = response.getStatus();
			if (status == 404){
				return null;
			}

			if(logger.isTraceEnabled()){
				String trace = response.getEntity(String.class);
				logger.trace(trace);
			}

			throw new StanbolServiceException("[HTTP " + status + "] Error retrieving Entity from the site");

		}catch(ClientHandlerException e){
			String message = "Error Parsing Entity Get Service Response";
			if(logger.isTraceEnabled())
				logger.trace(message, e);
			throw new StanbolServiceException(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#create(java.io.InputStream, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public String create(InputStream is, String id, Boolean update) throws StanbolServiceException
	{
		URI uri = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path("entity").
				queryParam("id", id).
				queryParam("update", update).
				build();

		//    	javax.ws.rs.client.Entity<?> entity = 
		//    			javax.ws.rs.client.Entity.entity(is, );

		try{
			RestClientExecutor.post(uri, 
					is, 
					MediaType.TEXT_XML_TYPE,
					new MediaType("application", "rdf+xml"),
					Collections.<String, String> emptyMap(),
					String.class);
			if (logger.isDebugEnabled())
			{
				logger.debug("Entity " + id + " has been sucessfully created");
			}

			return id;
		}catch(UniformInterfaceException e){
			int status = e.getResponse().getStatus();

			if(logger.isTraceEnabled())
				logger.trace(e.getResponse().getEntity(String.class));

			if (status == 400)
				throw new StanbolServiceException(id.toString()
						+ " already exists within EntityHub. You might want to pass updated param with a true value");

			throw new StanbolServiceException("[HTTP " + status
					+ "] Error while posting content into stanbol EntityHub");

		}catch(ClientHandlerException e){
			String message = "Error parsing EntityHub Entity create service response";
			if(logger.isTraceEnabled())
				logger.trace(message, e);
			throw new StanbolServiceException(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#create(org.apache.stanbol.client.entityhub.model.Entity, java.lang.Boolean)
	 */
	@Override
	public String create(Entity entity, Boolean update) throws StanbolServiceException
	{
		return create(entity.getStream(), entity.getUri(), update); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#update(java.io.InputStream, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Entity update(InputStream is, String id, Boolean create) throws StanbolServiceException
	{
		UriBuilder createBuilder = builder.
				clone().
				path(STANBOL_ENTITYHUB_PATH).
				path("entity");

		if (id != null && !id.equals(""))
			createBuilder = createBuilder.queryParam("id", id);
		if (!create)
			createBuilder = createBuilder.queryParam("create", create.toString());

		URI uri = createBuilder.build();

		try{
			InputStream response = RestClientExecutor.post(uri, 
					is, 
					new MediaType("application", "rdf+xml"),
					new MediaType("application", "rdf+xml"),
					Collections.<String, String> emptyMap(),
					InputStream.class);

			if (logger.isDebugEnabled())
			{
				logger.debug("Entity " + id + " has been sucessfully updated");
			}

			return parse(id, response, false);
		}catch(UniformInterfaceException e){
			int status = e.getResponse().getStatus();

			if(logger.isTraceEnabled())
				logger.trace(e.getResponse().getEntity(String.class));

			if (status == 400)
				throw new StanbolServiceException(id.toString()
						+ " already exists within EntityHub. You might want to pass updated param with a true value");

			throw new StanbolServiceException("[HTTP " + status
					+ "] Error while posting content into stanbol EntityHub");

		}catch(ClientHandlerException e){
			String message = "Error parsing EntityHub Entity update service response";
			if(logger.isTraceEnabled())
				logger.trace(message, e);
			throw new StanbolServiceException(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#update(org.apache.stanbol.client.entityhub.model.Entity, java.lang.Boolean)
	 */
	@Override
	public Entity update(Entity entity, Boolean create) throws StanbolServiceException
	{
		return update(entity.getStream(), entity.getUri(), create); 
	}


	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#delete(java.lang.String)
	 */
	@Override
	public Boolean delete(String id) throws StanbolServiceException
	{
		return deleteAux(id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#deleteAll()
	 */
	@Override
	public Boolean deleteAll() throws StanbolServiceException
	{
		return deleteAux("*");
	}

	private Boolean deleteAux(String id) throws StanbolServiceException
	{
		URI uri = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path("entity").
				queryParam("id", id).
				build();

		ClientResponse response = RestClientExecutor.delete(uri);

		int status = response.getStatus();

		if (status == 404)
			return false;

		if (status != 200 && status != 201 && status != 202)
		{
			if(logger.isTraceEnabled())
				logger.trace(response.getEntity(String.class));
			throw new StanbolServiceException("[HTTP " + status + "] Error while deleting content into stanbol server");
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("Entity " + id + " has been sucessfully deleted at " + response.getLocation());
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.EntityHub#lookup(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Entity lookup(String id, Boolean create) throws StanbolServiceException
	{
		URI uri = 
				builder.clone().path(STANBOL_ENTITYHUB_PATH).
				path("lookup").
				queryParam("id", id).
				queryParam("create", create.toString()).
				build();

		try{
			InputStream response = RestClientExecutor.get(uri, 
					new MediaType("application", "rdf+xml"),
					Collections.<String, String> emptyMap(),
					InputStream.class);

			if (logger.isDebugEnabled())
			{
				logger.debug("Entity " + id + " has been sucessfully looked up");
			}

			return parse(id, response, true);
		}catch(UniformInterfaceException e){
			// Check HTTP status code
			int status = e.getResponse().getStatus();

			if(logger.isTraceEnabled())
				logger.trace(e.getResponse().getEntity(String.class));
			if (status == 404)
				return null;

			if (status == 403)
				throw new StanbolServiceException(
						"Creation of new Symbols is not allowed in the current Stanbol Configuration");

			throw new StanbolServiceException("[HTTP " + status + "] Error retrieving content from stanbol server");

		}catch(ClientHandlerException e){
			String message = "Error parsing Entity";
			if(logger.isTraceEnabled())
				logger.trace(message, e);
			throw new StanbolServiceException(message);
		}
    }

    /**
     * @throws StanbolServiceException
     * @see EntityHub#search(String, String, String, String, int, int)
     */
    @Override
    public Collection<Entity> search(String name, String field, String language, LDPathProgram ldpath, int limit, int offset)
            throws StanbolServiceException
    {
    	
    	UriBuilder findBuilder = 
    			builder.clone().path(STANBOL_ENTITYHUB_PATH).
    			path("find").
    			queryParam("name", name);
    	
    	if (field != null && !field.equals(""))
            findBuilder = findBuilder.queryParam("field", field);
        if (language != null && !language.equals(""))
        	findBuilder = findBuilder.queryParam("language", language);
        
        URI uri = findBuilder.queryParam("ldpath", ldpath.toString()).
        		queryParam("limit", "" + limit).
        		queryParam("offset", "" + offset).build();

        return searchAux(uri, name);
    }

    /**
     * @throws StanbolServiceException
     * @see EntityHub#search(String, String, String, String, String, int, int)
     */
    @Override
    public Collection<Entity> search(String site, String name, String field, String language, LDPathProgram ldpath,
            int limit, int offset) throws StanbolServiceException
    {
    	UriBuilder findBuilder = 
    			builder.clone().path(STANBOL_ENTITYHUB_PATH).
    			path(STANBOL_ENTITYHUB_SITE_PATH).
    			path(site).
    			path("find").
    			queryParam("name", name);
    	
    	if (field != null && !field.equals(""))
            findBuilder = findBuilder.queryParam("field", field);
        if (language != null && !language.equals(""))
        	findBuilder = findBuilder.queryParam("language", language);
        
        URI uri = findBuilder.queryParam("ldpath", ldpath.toString()).
        		queryParam("limit", "" + limit).
        		queryParam("offset", "" + offset).build();

        return searchAux(uri, name);    	
    }

    private List<Entity> searchAux(URI uri, String name) throws StanbolServiceException
    {
    	InputStream response = null; 
    			
    	try{
    		response = RestClientExecutor.get(uri, 
    				new MediaType("application", "rdf+xml"),
    				Collections.<String, String> emptyMap(),
    				InputStream.class);
    		if (logger.isDebugEnabled())
            {
                logger.debug("Entities by " + name + " has been found sucessfully");
            }
    		
    	}catch(UniformInterfaceException e){
    		if(logger.isTraceEnabled())
    			logger.trace(e.getResponse().getEntity(String.class));
            int status = e.getResponse().getStatus();
            throw new StanbolServiceException("[HTTP " + status + "] Error retrieving content from stanbol server");
    	}catch(ClientHandlerException e){
    	
    	}
        Model model = ModelFactory.createDefaultModel();
        model.read(response, null);

        List<Entity> result = Lists.newArrayList();
        ResIterator iterator = model.listSubjects();

        while (iterator.hasNext())
        {
            RDFNode next = iterator.next();
            if (next.isResource())
                if (!next.asResource().getURI().equals(
                        "http://stanbol.apache.org/ontology/entityhub/query#QueryResultSet"))
                    result.add(new Entity(next.asResource().getModel(), next.asResource().getURI()));
        }

        return result;
    }

    /**
     * @throws StanbolServiceException
     * @see EntityHub#ldpath(List, String)
     */
    @Override
    public Model ldpath(String context, LDPathProgram ldPathProgram) throws StanbolServiceException
    {
    	URI uri = 
    			builder.clone().path(STANBOL_ENTITYHUB_PATH).
    			path("ldpath").
    			queryParam("context", context.toString()).
    			queryParam("ldpath", ldPathProgram.toString()).
    			build();    	
        return ldpathAux(uri);
    }

    /**
     * @throws StanbolServiceException
     * @see EntityHub#ldpath(String, List, String)
     */
    @Override
    public Model ldpath(String site, String context, LDPathProgram ldPathProgram) throws StanbolServiceException
    {
    	URI uri = 
    			builder.clone().path(STANBOL_ENTITYHUB_PATH).
    			path(STANBOL_ENTITYHUB_SITE_PATH).
    			path(site).
    			path("ldpath").
    			queryParam("context", context.toString()).
    			queryParam("ldpath", ldPathProgram.toString()).
    			build();
        return ldpathAux(uri);
    }

    private Model ldpathAux(URI uri) throws StanbolServiceException
    {
    	InputStream response = null; 
    	try{		
    		response = RestClientExecutor.get(uri, 
    				new MediaType("application", "rdf+xml"),
    				Collections.<String, String> emptyMap(),
    				InputStream.class);
    		
    	}catch(UniformInterfaceException e){
    		if(logger.isTraceEnabled())
    			logger.trace(e.getResponse().getEntity(String.class));
    		throw new StanbolServiceException("[HTTP " + e.getResponse().getStatus() + "] Error retrieving content from stanbol server");
    	}catch(ClientHandlerException e){
    		String message = "Error parsing LDPath result";
    		if(logger.isTraceEnabled())
    			logger.trace(message, e);
    		throw new StanbolServiceException(message);
    	}

        Model model = ModelFactory.createDefaultModel();
        model.read(response, null);
        return model;
    }
    
    /**
     * Extract a content item from an InputStream
     * 
     * @param id Content id
     * @param is Content input stream
     * @return Content item
     */
    private Entity parse(String id, InputStream is, Boolean extractId)
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null);
        
        if (extractId)
            return new Entity(model, model.listSubjects().next().getURI().replace(".meta", ""));
        else
            return new Entity(model, id);
    }

}
