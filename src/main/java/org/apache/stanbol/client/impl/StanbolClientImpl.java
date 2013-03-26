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
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService;
import org.apache.stanbol.client.contenthub.search.services.impl.StanbolContenthubFeaturedSearchServiceImpl;
import org.apache.stanbol.client.contenthub.search.services.impl.StanbolContenthubSolrSearchServiceImpl;
import org.apache.stanbol.client.contenthub.store.services.StanbolContenthubStoreService;
import org.apache.stanbol.client.contenthub.store.services.impl.StanbolContenthubStoreServiceImpl;
import org.apache.stanbol.client.enhancer.services.StanbolEnhancerService;
import org.apache.stanbol.client.enhancer.services.impl.StanbolEnhancerServiceImpl;
import org.apache.stanbol.client.entityhub.services.StanbolEntityhubService;
import org.apache.stanbol.client.entityhub.services.impl.StanbolEntityhubServiceImpl;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.restclient.RestClientImpl;
import org.apache.stanbol.client.sparql.services.StanbolSparqlService;
import org.apache.stanbol.client.sparql.services.impl.StanbolSparqlServiceImpl;

import com.sun.jersey.api.client.Client;

/**
 * Implementation of {@link StanbolClient}
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class StanbolClientImpl implements StanbolClient
{

    // Stanbol services
    private final StanbolEnhancerService enhancer;
    private final StanbolContenthubStoreService contenthub;
    private final StanbolEntityhubService entityhub;
    private final StanbolContenthubFeaturedSearchService featuredSearch;
    private final StanbolContenthubSolrSearchService solrSearch;
    private final StanbolSparqlService sparql;

    /**
     * Constructor
     * 
     * @param endpoint URL to Stanbol server
     * @throws StanbolClientException
     */
    public StanbolClientImpl(String endpoint) throws StanbolClientException
    {
        final RestClient restClient = new RestClientImpl();
        restClient.setHttpClient(Client.create());

        try
        {
            restClient.setEndpoint(endpoint);
        }
        catch (MalformedURLException e)
        {
            throw new StanbolClientException(e.getMessage(), e);
        }

        enhancer = new StanbolEnhancerServiceImpl(restClient);
        entityhub = new StanbolEntityhubServiceImpl(restClient);
        contenthub = new StanbolContenthubStoreServiceImpl(restClient);
        featuredSearch = new StanbolContenthubFeaturedSearchServiceImpl(restClient);
        solrSearch = new StanbolContenthubSolrSearchServiceImpl(restClient);
        sparql = new StanbolSparqlServiceImpl(restClient);
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#enhancer()
     */
    @Override
    public StanbolEnhancerService enhancer()
    {
        return enhancer;
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#contenthub()
     */
    @Override
    public StanbolContenthubStoreService contenthub()
    {
        return contenthub;
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#entityhub()
     */
    @Override
    public StanbolEntityhubService entityhub()
    {
        return entityhub;
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#featuredSearch()
     */
    @Override
    public StanbolContenthubFeaturedSearchService featuredSearch()
    {
        return featuredSearch;
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#solrSearch()
     */
    @Override
    public StanbolContenthubSolrSearchService solrSearch()
    {
        return solrSearch;
    }

    /**
     * @see org.apache.stanbol.client.StanbolClient#sparql()
     */
    @Override
    public StanbolSparqlService sparql()
    {
        return sparql;
    }
}
