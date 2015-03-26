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
package org.apache.stanbol.client.enhancer.impl;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.stanbol.client.Enhancer;
import org.apache.stanbol.client.enhancer.model.EnhancementStructure;
import org.apache.stanbol.client.rest.RestClientExecutor;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Enhancer}
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 * 
 */
public class EnhancerImpl implements Enhancer
{

    private Logger logger = LoggerFactory.getLogger(EnhancerImpl.class);
    
    private UriBuilder builder;

    /**
     * Constructor
     * 
     */
    public EnhancerImpl(UriBuilder builder)
    {
        this.builder = builder;
    }
    
    /*
     * (non-Javadoc)
     * @see org.apache.stanbol.client.Enhancer#enhance(java.lang.String, org.apache.stanbol.client.enhancer.impl.EnhancerParameters)
     */
    @Override
    public EnhancementStructure enhance(EnhancerParameters parameters) throws StanbolServiceException {

    	UriBuilder enhancerBuilder = builder.
    			clone().
    			path(STANBOL_ENHANCER_PATH);

    	if(parameters.getChain() != DEFAULT_CHAIN)
    		enhancerBuilder.path(STANBOL_CHAIN_PATH).path(parameters.getChain());
    	
    	// TODO Include Dereferrencing stuff

    	Entity<?> entity = Entity.entity(parameters.getContent(), MediaType.TEXT_PLAIN_TYPE);
    	Response response = RestClientExecutor.post(enhancerBuilder.build(), entity, parameters.getOutputFormat());

    	int status = response.getStatus();
    	if (status != 200 && status != 201 && status != 202)
    	{
    		throw new StanbolServiceException("[HTTP " + status + "] Error while enhancing content into stanbol server");
    	}

    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Content has been sucessfully enhanced");
    	}

    	return response.readEntity(EnhancementStructure.class);
    }
}