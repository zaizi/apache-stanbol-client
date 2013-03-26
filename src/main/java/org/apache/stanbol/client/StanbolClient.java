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

import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService;
import org.apache.stanbol.client.contenthub.store.services.StanbolContenthubStoreService;
import org.apache.stanbol.client.enhancer.services.StanbolEnhancerService;
import org.apache.stanbol.client.entityhub.services.StanbolEntityhubService;
import org.apache.stanbol.client.sparql.services.StanbolSparqlService;

/**
 * Define operations for Stanbol Services
 * 
 * @author efoncubierta
 * @author rmartin
 * @author Rafa Haro
 * 
 */
public interface StanbolClient
{
    /**
     * Stanbol enhancer service
     * 
     * @return Enhancer service
     */
    public StanbolEnhancerService enhancer();

    /**
     * Stanbol contenthub service
     * 
     * @return Contenthub service
     */
    public StanbolContenthubStoreService contenthub();

    /**
     * Stanbol entityhub service
     * 
     * @return Entityhub service
     */
    public StanbolEntityhubService entityhub();

    /**
     * Stanbol ContentHub Featured Search Service
     * 
     * @return StanbolContenthubFeaturedSearchService
     */
    public StanbolContenthubFeaturedSearchService featuredSearch();

    /**
     * Stanbol ContentHub Solr Search Service
     * 
     * @return StanbolContenthubSolrSearchService
     */
    public StanbolContenthubSolrSearchService solrSearch();
    
    /**
     * Stanbol SPARQL Service
     * 
     * @return StanbolSparqlService
     */
    public StanbolSparqlService sparql();
}
