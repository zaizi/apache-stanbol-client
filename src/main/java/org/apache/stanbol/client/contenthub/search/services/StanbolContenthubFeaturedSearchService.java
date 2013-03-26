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
import org.apache.stanbol.client.contenthub.search.model.FeaturedSearchResult;
import org.apache.stanbol.client.services.StanbolService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol ContentHub FeaturedSearch
 * 
 * @author Rafa Haro
 * 
 */
public interface StanbolContenthubFeaturedSearchService extends StanbolService
{

    /**
     * 
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem as ContentHub Featured search result
     * along with all the Semantic Facets for the resultant list of ContentItems and the list of {@list RelatedKeyword}
     * for the query passed by paramater
     * 
     * @param index Index where the search is going to be performed
     * @param queryTerm A keyword a statement or a set of keywords which can be regarded as the query term
     * @param solrQuery Solr query string. This is the string format which is accepted by a Solr server. If this
     *            parameter exists, search is performed based on this solrQuery and any queryTerms are neglected
     * @param ontologyURI URI of the ontology in which related keywords will be searched
     * @param offset The offset of the document from which the resultant documents will start as the search result
     * @param limit Maximum number of resultant documents to be returned as the search result. offset and limit
     *            parameters can be used to make a pagination mechanism for search results
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     */
    public FeaturedSearchResult search(String index, String queryTerm, String solrQuery,
            Map<String, List<String>> facets, String ontologyURI, int offset, int limit) throws StanbolServiceException;

    /**
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem as ContentHub solr search result 
     * using the String query passed by parameter along with all the Semantic Facets for the resultant list of ContentItems 
     * and the list of {@list RelatedKeyword} for the query
     * 
     * @param index Index where the search is going to be performed
     * @param query Query string in solr format
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     * @throws StanbolServiceException
     */
    public FeaturedSearchResult solrSearch(String index, String query) throws StanbolServiceException;
    
    /**
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem as ContentHub solr search result 
     * using the {@link SolrQuery} passed by parameter along with all the Semantic Facets for the resultant list of ContentItems 
     * and the list of {@list RelatedKeyword} for the query
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query 
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     * @throws StanbolServiceException
     */
    public FeaturedSearchResult solrSearch(String index, SolrQuery query) throws StanbolServiceException;
    

    /**
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem grouped by facets as ContentHub solr search results 
     * using the String query passed by parameter. The result also contains the list of {@list RelatedKeyword} for the query
     * 
     * @param index Index where the search is going to be performed
     * @param query Query string in solr format
     * @param facetFields List of fields to be faceted
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     * @throws StanbolServiceException
     */
    public FeaturedSearchResult facetedSearch(String index, String query, List<String> facetFields)
            throws StanbolServiceException;
    
    /**
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem grouped by facets as ContentHub solr search result 
     * using the {@link SolrQuery} passed by parameter. The result also contains the list of {@list RelatedKeyword} for the query
     * 
     * @param index Index where the search is going to be performed
     * @param query Solr Query
     * @param facetFields List of fields to be faceted
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     * @throws StanbolServiceException
     */
    public FeaturedSearchResult facetedSearch(String index, SolrQuery query, List<String> facetFields)
            throws StanbolServiceException;

    /**
     * Return a {@link FeaturedSearchResult} object containing a list of ContentItem grouped by facets as a result of
     * ContentHub Featured Search service result using the String query and the Facets Constraints passed by parameter.
     * Each constraint should contain a list of Facet values for each Facet Field Name in the Map. 
     * The result also contains the list of {@list RelatedKeyword} for the query
     * 
     * @param index Index where the search is going to be performed
     * @param query Query String in Solr Format
     * @param facets Facets field name - field value Map
     * @return {@link FeaturedSearchResult} with the list of found ContentItem
     * @throws StanbolServiceException
     */
    public FeaturedSearchResult facetedSearch(String index, String query, Map<String, List<String>> constrainsts)
            throws StanbolServiceException;
}
