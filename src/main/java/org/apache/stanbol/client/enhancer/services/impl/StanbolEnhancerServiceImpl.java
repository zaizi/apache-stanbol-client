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
package org.apache.stanbol.client.enhancer.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.stanbol.client.enhancer.model.EnhancementResult;
import org.apache.stanbol.client.enhancer.services.StanbolEnhancerService;
import org.apache.stanbol.client.restclient.Parameters;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolServiceAbstract;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Implementation of {@link StanbolEnhancerService}
 * 
 * @author efoncubierta
 * 
 */
public class StanbolEnhancerServiceImpl extends StanbolServiceAbstract implements StanbolEnhancerService
{

    private Logger logger = Logger.getLogger(StanbolEnhancerServiceImpl.class);

    /**
     * Constructor
     * 
     * @param restClient REST Client
     */
    public StanbolEnhancerServiceImpl(RestClient restClient)
    {
        super(restClient);
    }

    /**
     * @see org.zaizi.stanbol.services.StanbolEnhancerService#enhance(java.io.File)
     */
    @Override
    public EnhancementResult enhance(String URI, File file) throws StanbolServiceException
    {
        return enhance(URI, file, DEFAULT_CHAIN);
    }

    /**
     * @see org.apache.stanbol.client.enhancer.services.StanbolEnhancerService#enhance(java.lang.String, java.io.File, java.lang.String)
     */
    @Override
    public EnhancementResult enhance(String URI, File file, String chain) throws StanbolServiceException
    {
        try
        {
            return enhance(URI, new FileInputStream(file), chain);
        }
        catch (FileNotFoundException e)
        {
            throw new StanbolServiceException(e.getMessage(), e);
        }
    }

    /**
     * @see org.apache.stanbol.client.enhancer.services.StanbolEnhancerService#enhance(java.lang.String, java.io.InputStream)
     */
    @Override
    public EnhancementResult enhance(String URI, InputStream is) throws StanbolServiceException
    {
        return enhance(URI, is, DEFAULT_CHAIN);

    }

    /**
     * @see org.apache.stanbol.client.enhancer.services.StanbolEnhancerService#enhance(java.lang.String, java.io.InputStream, java.lang.String)
     */
    @Override
    public EnhancementResult enhance(String URI, InputStream is, String chain) throws StanbolServiceException
    {
        // build the path using the chain
        String path = STANBOL_ENHANCER_PATH;
        if (!DEFAULT_CHAIN.equals(chain))
        {
            path += "/" + STANBOL_CHAIN_PATH + "/" + chain;
        }
        
        Parameters par = new Parameters();
        if(URI != null)
            par.put("uri", URI);

        ClientResponse response = getRestClient().post(path, is, MediaType.TEXT_PLAIN_TYPE,
                new MediaType("application", "rdf+xml"), par);

        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error while enhancing content into stanbol server");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Content has been sucessfully enhanced");
        }

        // Parse the RDF model
        Model model = ModelFactory.createDefaultModel();
        model.read(response.getEntityInputStream(), null);

        return new EnhancementResult(model);
    }

    /**
     * @throws StanbolServiceException 
     * @see org.apache.stanbol.client.enhancer.services.StanbolEnhancerService#enhance(java.lang.String, java.lang.String)
     */
    @Override
    public EnhancementResult enhance(String URI, String content) throws StanbolServiceException
    {
        return enhance(URI, content, DEFAULT_CHAIN);
    }

    /**
     * @throws StanbolServiceException 
     * @see org.apache.stanbol.client.enhancer.services.StanbolEnhancerService#enhance(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public EnhancementResult enhance(String URI, String content, String chain) throws StanbolServiceException
    {
        InputStream stream;
        try
        {
            stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new StanbolServiceException(e.getMessage());
        }
        return enhance(URI, stream, chain);
    }
}
