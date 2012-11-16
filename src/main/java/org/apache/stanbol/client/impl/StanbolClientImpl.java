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
package org.apache.stanbol.client.impl;

import java.net.MalformedURLException;

import org.apache.stanbol.client.StanbolClient;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.restclient.RestClientImpl;
import org.apache.stanbol.client.services.StanbolContenthubService;
import org.apache.stanbol.client.services.StanbolEnhancerService;
import org.apache.stanbol.client.services.StanbolEntityhubService;
import org.apache.stanbol.client.services.impl.StanbolContenthubServiceImpl;
import org.apache.stanbol.client.services.impl.StanbolEnhancerServiceImpl;
import org.apache.stanbol.client.services.impl.StanbolEntityhubServiceImpl;

import com.sun.jersey.api.client.Client;

/**
 * Implementation of {@link StanbolClient}
 * 
 * @author efoncubierta, rmartin
 * 
 */
public class StanbolClientImpl implements StanbolClient {
    
	// Stanbol services
	private final StanbolEnhancerService enhancer;
	private final StanbolContenthubService contenthub;
	private final StanbolEntityhubService entityhub;

	/**
	 * Constructor
	 * 
	 * @param endpoint
	 *            URL to Stanbol server
	 * @throws StanbolClientException
	 */
	public StanbolClientImpl(String endpoint) throws StanbolClientException {
		final RestClient restClient = new RestClientImpl();
		restClient.setHttpClient(Client.create());

		try {
			restClient.setEndpoint(endpoint);
		} catch (MalformedURLException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}

		enhancer = new StanbolEnhancerServiceImpl(restClient);
		entityhub = new StanbolEntityhubServiceImpl(restClient);
		contenthub = new StanbolContenthubServiceImpl(restClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.StanbolClient#enhancer()
	 */
	@Override
    public StanbolEnhancerService enhancer() {
	    return enhancer;
	}
    
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.client.StanbolClient#contenthub()
	 */
    @Override
    public StanbolContenthubService contenthub() {
        return contenthub;
    }
    
    /*
     * (non-Javadoc)
     * @see org.apache.stanbol.client.StanbolClient#entityhub()
     */
    @Override
    public StanbolEntityhubService entityhub() {
        return entityhub;
    }
}
