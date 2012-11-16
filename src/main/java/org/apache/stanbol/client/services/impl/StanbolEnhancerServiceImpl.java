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
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.model.parser.EnhancementParser;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.StanbolEnhancerService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Implementation of {@link StanbolEnhancerService}
 * 
 * @author efoncubierta
 *
 */
public class StanbolEnhancerServiceImpl
	extends StanbolServiceAbstract implements StanbolEnhancerService {
	
	private Logger logger = Logger.getLogger(StanbolEnhancerServiceImpl.class);
	
	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public StanbolEnhancerServiceImpl(RestClient restClient) {
		super(restClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolEnhancerService#enhance(java.io.File)
	 */
	@Override
	public List<Enhancement> enhance(File file) throws StanbolServiceException {
	    return enhance(file, DEFAULT_CHAIN);
	}
	
    @Override
    public List<Enhancement> enhance(File file, String chain) throws StanbolServiceException {
        try {
            return enhance(new FileInputStream(file), chain);
        } catch(FileNotFoundException e) {
            throw new StanbolServiceException(e.getMessage(), e);
        }
    }


	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolEnhancerService#enhance(java.io.InputStream)
	 */
	@Override
	public List<Enhancement> enhance(InputStream is) throws StanbolServiceException {
		return enhance(is, DEFAULT_CHAIN);

	}
	
    @Override
    public List<Enhancement> enhance(InputStream is, String chain) throws StanbolServiceException {
        // build the path using the chain
        String path = STANBOL_ENHANCER_PATH;
        if(!DEFAULT_CHAIN.equals(chain)) {
            path += "/" + STANBOL_CHAIN_PATH + "/" + chain;
        }
        
        ClientResponse response = getRestClient().post(path, is,
                MediaType.TEXT_PLAIN_TYPE, new MediaType("application", "rdf+xml"));
        
        int status = response.getStatus();
        if(status != 200 && status != 201 && status != 202) {
            throw new StanbolServiceException(
                    "[HTTP " + status + "] Error while enhancing content into stanbol server"
                );
        }
        
        if(logger.isDebugEnabled())
        {
            logger.debug("Content has been sucessfully enhanced");
        }
        
        // Parse the RDF model
        Model model = ModelFactory.createDefaultModel();
        model.read(response.getEntityInputStream(), null);
        
        return EnhancementParser.parse(model);
    }
}
