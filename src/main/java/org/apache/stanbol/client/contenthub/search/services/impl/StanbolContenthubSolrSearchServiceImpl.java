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
package org.apache.stanbol.client.contenthub.search.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.stanbol.client.contenthub.search.model.SearchResultParser;
import org.apache.stanbol.client.contenthub.search.model.SolrSearchResult;
import org.apache.stanbol.client.contenthub.search.model.StanbolSolrVocabulary;
import org.apache.stanbol.client.contenthub.search.model.StanbolSolrVocabulary.SolrFieldName;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolServiceAbstract;

/**
 * Implementation of {@link StanbolContenthubSolrSearchService}
 * 
 * @author Rafa Haro
 * 
 */
public class StanbolContenthubSolrSearchServiceImpl extends StanbolServiceAbstract implements
        StanbolContenthubSolrSearchService
{
    private Logger logger = Logger.getLogger(StanbolContenthubSolrSearchServiceImpl.class);

    private Map<String, SolrServer> solrServers;

    /**
     * Constructor
     * 
     * @param restClient REST Client
     */
    public StanbolContenthubSolrSearchServiceImpl(RestClient restClient)
    {
        super(restClient);
        solrServers = new HashMap<String, SolrServer>();
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#search(java.lang.String, java.lang.String)
     */
    @Override
    public SolrSearchResult search(String index, String query) throws StanbolServiceException
    {
        SolrQuery sQuery = new SolrQuery();
        sQuery.setQuery(query);
        return search(index, sQuery);

    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#search(java.lang.String, org.apache.solr.client.solrj.SolrQuery)
     */
    @Override
    public SolrSearchResult search(String index, SolrQuery query) throws StanbolServiceException
    {
        SolrServer server = getSolrServer(index);
        // String escaped = ClientUtils.escapeQueryChars(query.toString());
        // query.setQuery(query);
        try
        {
            QueryResponse response = server.query(query);

            if (logger.isDebugEnabled())
                logger.debug(" Query Request to Stanbol Solr index " + index + ": " + query.toString()
                        + " executed. Got Response: " + response.toString());

            return SearchResultParser.queryResponse2SolrSearchResult(index, response, server,
                    getRestClient().getEndpoint());
        }
        catch (SolrServerException e)
        {
            e.printStackTrace();
            throw new StanbolServiceException("Error querying Stanbol Solr Index " + index + ". Error message: "
                    + e.getMessage());
        }
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public SolrSearchResult facetedSearch(String index, String query, List<String> facetFields)
            throws StanbolServiceException
    {
        SolrQuery sQuery = new SolrQuery();
        sQuery.setQuery(query);
        return facetedSearch(index, sQuery, facetFields);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, java.lang.String, java.util.Map)
     */
    public SolrSearchResult facetedSearch(String index, String query, Map<String, List<String>> facets)
            throws StanbolServiceException
    {
        SolrQuery sQuery = new SolrQuery();
        sQuery.setQuery(query);
        return facetedSearch(index, sQuery, facets);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, org.apache.solr.client.solrj.SolrQuery, java.util.List)
     */
    @Override
    public SolrSearchResult facetedSearch(String index, SolrQuery query, List<String> facetFields)
            throws StanbolServiceException
    {
        SolrQuery sQuery = new SolrQuery();
        sQuery.setQuery(query.getQuery()).setFacet(true).setFacetMinCount(1);

        for (String field : facetFields)
            sQuery.addFacetField(field);
        return search(index, sQuery);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, org.apache.solr.client.solrj.SolrQuery, java.util.Map)
     */
    public SolrSearchResult facetedSearch(String index, SolrQuery query, Map<String, List<String>> facets)
            throws StanbolServiceException
    {
        // SolrQuery query = new SolrQuery();
        // query.setQuery(query.getQuery()).setFacet(true).setFacetMinCount(1);
        query.setFacet(true).setFacetMinCount(1);

        NamedList<Object> fieldList = null;
        if (facets != null)
        {
            try
            {
                fieldList = SearchResultParser.getAllFacetFields(getSolrServer(index));
                setFacetFields(query, fieldList);
            }
            catch (SolrServerException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException(e.getMessage(), e);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException(e.getMessage(), e);
            }

            Iterator<Entry<String, List<String>>> it = facets.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, List<String>> nextFacet = it.next();
                String fieldName = ClientUtils.escapeQueryChars(nextFacet.getKey());
                String type = SearchResultParser.getFacetFieldType(fieldName, fieldList);
                for (String facetValue : nextFacet.getValue())
                {
                    if (type != null && StanbolSolrVocabulary.isRangeType(type))
                        query.addFilterQuery(fieldName + ":" + facetValue);
                    else
                        query.addFilterQuery(fieldName + ":\"" + ClientUtils.escapeQueryChars(facetValue) + "\"");
                }
            }
        }

        return search(index, query);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, org.apache.solr.client.solrj.SolrQuery, java.util.Map, java.util.List)
     */
    @Override
    public SolrSearchResult facetedSearch(String index, SolrQuery query, Map<String, List<String>> facets,
            List<String> facetFields) throws StanbolServiceException
    {
        // SolrQuery query = new SolrQuery();
        // query.setQuery(query.getQuery()).setFacet(true).setFacetMinCount(1);
        query.setFacet(true).setFacetMinCount(1);

        for (String facetField : facetFields)
            query.addFacetField(facetField);

        NamedList<Object> fieldList = null;
        if (facets != null)
        {
            try
            {
                fieldList = SearchResultParser.getAllFacetFields(getSolrServer(index));
            }
            catch (SolrServerException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException(e.getMessage(), e);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException(e.getMessage(), e);
            }

            Iterator<Entry<String, List<String>>> it = facets.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, List<String>> nextFacet = it.next();
                String fieldName = ClientUtils.escapeQueryChars(nextFacet.getKey());
                String type = SearchResultParser.getFacetFieldType(fieldName, fieldList);
                for (String facetValue : nextFacet.getValue())
                {
                    if (type != null && StanbolSolrVocabulary.isRangeType(type))
                        query.addFilterQuery(fieldName + ":" + facetValue);
                    else
                        query.addFilterQuery(fieldName + ":\"" + ClientUtils.escapeQueryChars(facetValue) + "\"");
                }
            }
        }

        return search(index, query);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#facetedSearch(java.lang.String, java.lang.String, java.util.Map, java.util.List)
     */
    @Override
    public SolrSearchResult facetedSearch(String index, String query, Map<String, List<String>> facets,
            List<String> facetFields) throws StanbolServiceException
    {
        return facetedSearch(index, new SolrQuery(query), facets, facetFields);
    }
    
    
    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService#getFieldType(java.lang.String, java.lang.String)
     */
    @Override
    public String getFieldType(String index, String fieldName) throws StanbolServiceException
    {
        String type = null;
        NamedList<Object> fieldList = null;
        try
        {
            fieldList = SearchResultParser.getAllFacetFields(getSolrServer(index));
        }
        catch (SolrServerException e)
        {
            e.printStackTrace();
            throw new StanbolServiceException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new StanbolServiceException(e.getMessage(), e);
        }

        type = SearchResultParser.getFacetFieldType(fieldName, fieldList);

        return type;
    }

    /**
     * Return or create a new SolrServer instance for the index passed by parameter
     * 
     * @param index ContentHub index name
     * @return SolrServer instance
     * @throws StanbolServiceException
     */
    private SolrServer getSolrServer(String index) throws StanbolServiceException
    {

        SolrServer server = solrServers.get(index);

        if (server == null)
        {
            /* Setting SolrServer */

            server = new HttpSolrServer(getRestClient().getEndpoint() + STANBOL_CONTENTHUB_SOLR_PATH + index, null,
                    new XMLResponseParser());

            ((HttpSolrServer) server).setMaxRetries(1);
            ((HttpSolrServer) server).setConnectionTimeout(10000); // TODO Make this configurable

            solrServers.put(index, server);
        }

        return server;
    }

    /**
     * This methods adds a facet field the given <code>solrQuery</code> for each facet passed in
     * <code>allAvailableFacetNames</code>. This provides obtaining information about specified facets such as possible
     * facet values, number documents of documents matching with certain values of facets, etc in the search results.
     * 
     * @param solrQuery {@link SolrQuery} to be extended with facet fields
     * @param fieldList list of facets
     */
    private void setFacetFields(SolrQuery solrQuery, NamedList<Object> fieldList)
    {

        List<String> facetNames = new ArrayList<String>();
        for (int i = 0; i < fieldList.size(); i++)
            facetNames.add(fieldList.getName(i));

        solrQuery.setFields("*", "score");
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(1);
        if (fieldList != null)
        {
            for (String facet : facetNames)
            {
                if (SolrFieldName.CREATIONDATE.toString().equals(facet)
                        || (!SolrFieldName.isNameReserved(facet) && !StanbolSolrVocabulary.isNameExcluded(facet)))
                {
                    solrQuery.addFacetField(facet);
                }
            }
        }
    }
}