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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.stanbol.client.model.ContentHubDocument;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.model.LDPathProgram;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.StanbolContenthubService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Implementation of {@link StanbolContenthubService}
 * 
 * @author efoncubierta
 * 
 */
public class StanbolContenthubServiceImpl extends StanbolServiceAbstract implements StanbolContenthubService {
    private Logger logger = Logger.getLogger(StanbolContenthubServiceImpl.class);

    /**
     * Constructor
     * 
     * @param restClient
     *            REST Client
     */
    public StanbolContenthubServiceImpl(RestClient restClient) {
        super(restClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.stanbol.client.services.StanbolContenthubService#add(org.apache
     * .stanbol.client.services.ContentHubRequest)
     */
    @Override
    public URI add(ContentHubDocument request) throws StanbolServiceException {
        final String contentUrl = STANBOL_CONTENTHUB_PATH + request.getIndex() + STANBOL_CONTENTHUB_STORE_PATH;

        MultivaluedMapImpl fdmp = new MultivaluedMapImpl();
        if (request.getId() != null) {
            fdmp.add("id", request.getId());
        }
        if (request.getTitle() != null) {
            fdmp.add("title", request.getTitle());
        }
        if (request.getContentUrl() != null) {
            fdmp.add("url", request.getContentUrl());
        }
        if (request.getMetadata() != null) {
            // fdmp.field("constraints", request.getMetadata());
        }
        try {
            if (request.getContentStream() != null) {
                fdmp.add("content", IOUtils.toString(request.getContentStream()));
            } else if (request.getContentFile() != null) {

                fdmp.add("content", new FileInputStream(request.getContentFile()));

            }
        } catch (FileNotFoundException e) {
            throw new StanbolServiceException(e.getMessage(), e);
        } catch (IOException e) {
            throw new StanbolServiceException(e.getMessage(), e);
        }

        ClientResponse response = getRestClient().post(contentUrl, fdmp, MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                MediaType.WILDCARD_TYPE);

        int status = response.getStatus();
        if (status != 303) {
            throw new StanbolServiceException("[HTTP " + status + "] Error while posting content into stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Content " + request.getId() + " has been sucessfully created at " + response.getLocation());
        }

        return response.getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.stanbol.client.services.StanbolContenthubService#delete(org
     * .apache.stanbol.client.services.ContentHubRequest)
     */
    @Override
    public void delete(ContentHubDocument request) throws StanbolServiceException {
        delete(request.getIndex(), request.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.stanbol.client.services.StanbolContenthubService#delete(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void delete(String index, String id) throws StanbolServiceException {
        final String contentUrl = STANBOL_CONTENTHUB_PATH + index + STANBOL_CONTENTHUB_STORE_PATH + id;

        // Delete content from Stanbol server
        ClientResponse response = getRestClient().delete(contentUrl);

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status + "] Error deleting content from stanbol server");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.stanbol.client.services.StanbolContenthubService#get(org.apache
     * .stanbol.client.services.ContentHubRequest)
     */
    @Override
    public ContentItem get(ContentHubDocument request) throws StanbolServiceException {
        return get(request.getIndex(), request.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.stanbol.client.services.StanbolContenthubService#get(java.
     * lang.String, java.lang.String)
     */
    @Override
    public ContentItem get(String index, String id) throws StanbolServiceException {
        final String contentUrl = STANBOL_CONTENTHUB_PATH + index +
                                  STANBOL_CONTENTHUB_STORE_PATH +
                                  STANBOL_CONTENTHUB_METADATA_PATH + id;

        // Retrieve metadata from Stanbol server
        ClientResponse response = getRestClient().get(contentUrl, new MediaType("application", "rdf+xml"));

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException("[HTTP " + status + "] Error retrieving content from stanbol server");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Content " + id + " has been sucessfully loaded from " + contentUrl);
        }

        return parse(id, response.getEntityInputStream());
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
    private ContentItem parse(String id, InputStream is) {
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null);

        return new ContentItem(id, model);
    }

    @Override
    public void createIndex(String name, LDPathProgram program)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, LDPathProgram> getIndexes()
    {
        // TODO Auto-generated method stub
        return null;
    }
}