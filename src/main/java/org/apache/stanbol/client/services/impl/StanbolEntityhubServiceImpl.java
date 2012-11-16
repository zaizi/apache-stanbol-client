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
package org.apache.stanbol.client.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.stanbol.client.model.Entity;
import org.apache.stanbol.client.model.LDPathProgram;
import org.apache.stanbol.client.restclient.Parameters;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.StanbolEntityhubService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.sun.jersey.api.client.ClientResponse;

public class StanbolEntityhubServiceImpl extends StanbolServiceAbstract
		implements StanbolEntityhubService {

	private Logger logger = Logger.getLogger(StanbolEntityhubServiceImpl.class);
	
	/**
	 * Constructor
	 * 
	 * @param restClient
	 *            REST Client
	 */
	public StanbolEntityhubServiceImpl(RestClient restClient) {
		super(restClient);
	}

	/**
	 * Extract a content item from an InputStream
	 * 
	 * @param id
	 *            Content id
	 * @param is
	 *            Content input stream
	 * @return Content item
	 */
	private Entity parse(String id, InputStream is, Boolean extractId) {
		Model model = ModelFactory.createDefaultModel();
		model.read(is, null);
			
		if(extractId)
		    return new Entity(model, model.listSubjects().next().getURI().replace(".meta", ""));
		else
		    return new Entity(model, id);
	}
	
    @Override
    public List<URL> getReferencedSites() throws StanbolServiceException
    {
        ClientResponse response = getRestClient().get(STANBOL_ENTITYHUB_SITEMANAGER_PATH+"referenced",
                new MediaType("application", "rdf+xml"));

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Sites sucessfully retrieved from "
                    + response.getLocation());
        }
        
        List<URL> result = new ArrayList<URL>();
        JSONArray array = response.getEntity(JSONArray.class);
        String url = null;
        for(int i = 0; i < array.length(); i++)
            try{
                url = array.getString(i);
                result.add(new URL(url));
            }
            catch (MalformedURLException e){
                logger.error("Bad ReferencedSite URL " + url);
            }
            catch (JSONException e){
                String message = "Malformed JSON response for EntityHub referenced service";
                logger.error(message);
                throw new StanbolServiceException(message);
            }
        
        return result;
    }

    /**
     * @see StanbolEntityhubService#get(URI)
     */
    @Override
    public Entity get(URI id) throws StanbolServiceException
    {
        return getAux(id.toString(), STANBOL_ENTITYHUB_PATH + "entity");
    }

    /**
     * @see StanbolEntityhubService#get(String, URI)
     */
    @Override
    public Entity get(String site, URI id) throws StanbolServiceException
    {
        return getAux(id.toString(), STANBOL_ENTITYHUB_SITE_PATH+site+"/entity");
    }

    private Entity getAux(String id, String siteLocation) throws StanbolServiceException{
        
        Parameters par = new Parameters();
        par.put("id", id);

        ClientResponse response = getRestClient().get(siteLocation,
                new MediaType("application", "rdf+xml"), par);

        // Check HTTP status code
        int status = response.getStatus();
        if(status == 404)
            return null;
            
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Content " + id + " has been sucessfully loaded from "
                    + response.getLocation());
        }
        
        return parse(id.toString(), response.getEntityInputStream(), false);
    }

    /** 
     * @see StanbolEntityhubService#create(InputStream, URI, Boolean)
     */
    @Override
    public URI create(InputStream is, URI id, Boolean update) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        if (id != null && !id.equals(""))
            par.put("id", id.toString());
        if(update)
            par.put("update", update.toString());
        
        ClientResponse response = getRestClient().post(STANBOL_ENTITYHUB_PATH + "entity", is,
                new MediaType("application", "rdf+xml"), MediaType.TEXT_XML_TYPE, par); 
        
        int status = response.getStatus();
        
        if(status == 400)
            throw new StanbolServiceException(
                    id.toString() + " already exists within EntityHub. You might want to pass updated param with a true value"
                );
        
        if(status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException(
                    "[HTTP " + status + "] Error while posting content into stanbol EntityHub"
                );
        }
                       
        if(logger.isDebugEnabled()) {
            logger.debug("Entity " + id +
                         " has been sucessfully created at " + response.getLocation());
        }
        
        
        return response.getLocation();
    }

    /**
     * @see StanbolEntityhubService#create(File, URI, Boolean)
     */
    @Override
    public URI create(File file, URI id, Boolean update) throws StanbolServiceException
    {
        try {
            return create(new FileInputStream(file),id, update);
        } catch(FileNotFoundException e) {
            if(logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new StanbolServiceException(e.getMessage(), e);
        }
    }
    
    @Override
    public Entity update(InputStream is, URI id, Boolean create) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        if (id != null && !id.equals(""))
            par.put("id", id.toString());
        if(!create)
            par.put("create", create.toString());
        
        ClientResponse response = getRestClient().post(STANBOL_ENTITYHUB_PATH + "entity", is,
                new MediaType("application", "rdf+xml"), new MediaType("application", "rdf+xml"), par); 
        
        int status = response.getStatus();
        
        if(status == 400)
            throw new StanbolServiceException(
                    id.toString() + " already exists within EntityHub. You might want to pass updated param with a true value"
                );
        
        if(status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException(
                    "[HTTP " + status + "] Error while posting content into stanbol EntityHub"
                );
        }
               
        if(logger.isDebugEnabled()) {
            logger.debug("Entity " + id +
                         " has been sucessfully updated at " + response.getLocation());
        }
        
        
        return parse(id.toString(), response.getEntityInputStream(),false);
    }

    @Override
    public Entity update(File file, URI id, Boolean create) throws StanbolServiceException
    {
        try {
            return update(new FileInputStream(file),id, create);
        } catch(FileNotFoundException e) {
            if(logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new StanbolServiceException(e.getMessage(), e);
        }
    }

    /**
     * @see StanbolEntityhubService#delete(URI)
     */
    @Override
    public Boolean delete(URI id) throws StanbolServiceException
    {
        return deleteAux(id.toString());
    }

    /**
     * @see StanbolEntityhubService#deleteAll()
     */
    @Override
    public Boolean deleteAll() throws StanbolServiceException
    {
        return deleteAux("*");
    }
    
    private Boolean deleteAux(String id) throws StanbolServiceException
    {
        Parameters parameters = new Parameters();
        parameters.put("id", id);
        
        ClientResponse response = getRestClient().delete(STANBOL_ENTITYHUB_PATH + "entity",parameters);
        int status = response.getStatus();
        
        if(status == 404)
            return false;
        
        if(status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException(
                    "[HTTP " + status + "] Error while deleting content into stanbol server"
                );
        }
        
        if(logger.isDebugEnabled()) {
            logger.debug("Content " + id +
                         " has been sucessfully created at " + response.getLocation());
        }
        
        return true;
    }

    /**
     * @see StanbolEntityhubService#lookup(String, Boolean)
     */
    @Override
    public Entity lookup(String id, Boolean create) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        par.put("id", id);
        par.put("create", create.toString());

        ClientResponse response = getRestClient().get(STANBOL_ENTITYHUB_PATH+"lookup",
                new MediaType("application", "rdf+xml"), par);
        
        // Check HTTP status code
        int status = response.getStatus();
        
        if(status == 404)
            return null;
        
        if(status == 403)
            throw new StanbolServiceException("Creation of new Symbols is not allowed in the current Stanbol Configuration");
        
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Entity " + id + " has been sucessfully looked up from "
                    + response.getLocation());
        }
        
        return parse(id.toString(), response.getEntityInputStream(), true);
    }

    /**
     * @throws StanbolServiceException 
     * @see StanbolEntityhubService#search(String, String, String, String, int, int)
     */
    @Override
    public List<Entity> search(String name, String field, String language, LDPathProgram ldpath, int limit, int offset) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        par.put("name", name);
        if(field != null && !field.equals(""))
            par.put("field", field);
        if(language != null && !language.equals(""))
            par.put("language", language);
        par.put("ldpath", ldpath.toString());
        par.put("limit",""+limit);
        par.put("offset",""+offset);
        
        return searchAux(par, STANBOL_ENTITYHUB_PATH+"find");                
    }

    /**
     * @throws StanbolServiceException 
     * @see StanbolEntityhubService#search(String, String, String, String, String, int, int)
     */
    @Override
    public List<Entity> search(String site, String name, String field, String language, LDPathProgram ldpath, int limit,
            int offset) throws StanbolServiceException
    {
        Parameters par = new Parameters();
        par.put("name", name);
        if(field != null && !field.equals(""))
            par.put("field", field);
        if(language != null && !language.equals(""))
            par.put("language", language);
        par.put("ldpath", ldpath.toString());
        par.put("limit",""+limit);
        par.put("offset",""+offset);
        
        return searchAux(par, STANBOL_ENTITYHUB_SITE_PATH + site + "/find");
    }
    
    private List<Entity> searchAux(Parameters p, String location) throws StanbolServiceException
    {
        ClientResponse response = getRestClient().get(location,
                new MediaType("application", "rdf+xml"), p);
        
        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Entities by " + p.get("name") + " has been found sucessfully "
                    + response.getLocation());
        }
  
        Model model = ModelFactory.createDefaultModel();
        model.read(response.getEntityInputStream(), null);
             
        List<Entity> result = new ArrayList<Entity>();
        ResIterator iterator = model.listSubjects();
        
        while(iterator.hasNext()){
            RDFNode next = iterator.next();
            if(next.isResource())
                if(!next.asResource().getURI().equals("http://stanbol.apache.org/ontology/entityhub/query#QueryResultSet"))
                    result.add(new Entity(next.asResource().getModel(), next.asResource().getURI()));
       }
                 
        return result;
    }

    /**
     * @throws StanbolServiceException 
     * @see StanbolEntityhubService#ldpath(List, String)
     */
    @Override
    public Model ldpath(URI context, LDPathProgram ldPathProgram) throws StanbolServiceException
    {
        return ldpathAux(STANBOL_ENTITYHUB_PATH + "/ldpath", context.toString(), ldPathProgram.toString());
    }

    /**
     * @throws StanbolServiceException 
     * @see StanbolEntityhubService#ldpath(String, List, String)
     */
    @Override
    public Model ldpath(String site, URI context, LDPathProgram ldPathProgram) throws StanbolServiceException
    {
       return ldpathAux(STANBOL_ENTITYHUB_SITE_PATH + site + "/ldpath", context.toString(), ldPathProgram.toString());
    }
    
    private Model ldpathAux(String location, String context, String ldPathProgram) throws StanbolServiceException{
        
        Parameters par = new Parameters();
        par.put("context", context);
        par.put("ldpath", ldPathProgram);

        ClientResponse response = getRestClient().get(location,
                new MediaType("application", "rdf+xml"), par);
        
        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("LDPath Program " + ldPathProgram + " executed sucessfully in "
                    + response.getLocation() + " over the context " + context);
        }
        
        Model model = ModelFactory.createDefaultModel();
        model.read(response.getEntityInputStream(), null);
        return model;
    }
	
}
