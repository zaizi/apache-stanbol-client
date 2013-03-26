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
package org.apache.stanbol.client.contenthub.search.services;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.stanbol.client.contenthub.search.model.DocumentResult;
import org.apache.stanbol.client.contenthub.search.model.SolrSearchResult;
import org.apache.stanbol.client.services.StanbolService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol ContentHub SolrSearch
 * 
 * @author Rafa Haro
 * 
 */
public interface StanbolContenthubSolrSearchService extends StanbolService
{
    /**
     * ContentHub Solr Path
     */
    static final String STANBOL_CONTENTHUB_SOLR_PATH = "solr/default/";
    

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem as a result of ContentHub 
     * solr search using the String query with a proper SolrQuery Format passed by parameter. 
     * Each ContentItem is parsed from a {@link SolrDocument} and is stored in the result as a {@link DocumentResult}
     * 
     * @param index Index where the search is going to be performed
     * @param query Query string in solr format
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult search(String index, String query) throws StanbolServiceException;

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem as a result of ContentHub 
     * solr search using the {@link SolrQuery} passed by parameter. Each ContentItem is parsed 
     * from a {@link SolrDocument} and is stored in the result as a {@link DocumentResult}
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query Object
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult search(String index, SolrQuery query) throws StanbolServiceException;

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem grouped by facets as 
     * a result of ContentHub solr search using the String query with a proper SolrQuery Format passed 
     * by parameter. Each ContentItem is parsed from a {@link SolrDocument} and is stored 
     * in the result as a {@link DocumentResult}
     * 
     * @param index Index where the search is going to be performed
     * @param query Query string in solr format
     * @param facetFields List of fields to be faceted
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, String query, List<String> facetFields)
            throws StanbolServiceException;

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem as a result of ContentHub
     * Solr Search using the String query with a proper SolrQuery Format and the Facets Constraints passed 
     * by parameter. Each constraint should contain a list of Facet values for each Facet Field Name in the Map. 
     * 
     * @param index Index where the search is going to be performed
     * @param query Query String in Solr Format
     * @param facets Facets field name - field value Map
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, String query, Map<String, List<String>> facets)
            throws StanbolServiceException;

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem grouped by facets as 
     * ContentHub solr search results using the {@link SolrQuery} passed by parameter. Each ContentItem
     * is parsed from a {@link SolrDocument} and is stored in the result as a {@link DocumentResult}
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query Object
     * @param facetFields List of fields to be faceted
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, SolrQuery query, List<String> facetFields)
            throws StanbolServiceException;

    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem as a result of ContentHub
     * Solr Search using the {@link SolrQuery} and the Facets Constraints passed by parameter. 
     * Each constraint should contain a list of Facet values for each Facet Field Name in the Map.
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query Object
     * @param facets Facets field name - field value Map
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, SolrQuery query, Map<String, List<String>> facets)
            throws StanbolServiceException;
    
    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem grouped by the passed list
     * of facets fields as a result of ContentHub Solr Search using the {@link SolrQuery} and the 
     * Facets Constraints passed by parameter. Each constraint should contain a list of Facet values for each
     * Facet Field Name in the Map.
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query Object
     * @param facets Facets field name - field value Map
     * @param facetFields List of Facet Fields
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, SolrQuery query, Map<String, List<String>> facets, 
            List<String> facetFields) throws StanbolServiceException;
    
    /**
     * Return a {@link SolrSearchResult} object containing a list of ContentItem grouped by the passed list
     * of facets fields as a result of ContentHub Solr Search using the String query with a proper SolrQuery 
     * Format and the Facets Constraints passed by parameter. Each constraint should contain a list of Facet
     * values for each Facet Field Name in the Map.
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr String Query
     * @param facets Facets field name - field value Map
     * @param facetFields List of Facet Fields
     * @return List of found ContentItem
     * @throws StanbolServiceException
     */
    public SolrSearchResult facetedSearch(String index, String query, Map<String, List<String>> facets, 
            List<String> facetFields) throws StanbolServiceException;
    
    /**
     * Return Facet Field type by Facet Name
     * 
     * @param index ContentHub index
     * @param fieldName Name of the Facet Field
     * @return Facet Type
     * @throws StanbolServiceException
     */
    public String getFieldType(String index, String fieldName) throws StanbolServiceException;
}
