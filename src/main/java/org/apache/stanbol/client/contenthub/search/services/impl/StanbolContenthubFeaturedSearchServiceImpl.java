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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.stanbol.client.contenthub.search.model.FacetResult;
import org.apache.stanbol.client.contenthub.search.model.FeaturedSearchResult;
import org.apache.stanbol.client.contenthub.search.model.SearchResultParser;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService;
import org.apache.stanbol.client.contenthub.store.services.StanbolContenthubStoreService;
import org.apache.stanbol.client.restclient.Parameters;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolServiceAbstract;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Implementation of {@link StanbolContenthubFeaturedSearchService}
 * 
 * @author Rafa Haro
 * 
 */
public class StanbolContenthubFeaturedSearchServiceImpl extends StanbolServiceAbstract implements
        StanbolContenthubFeaturedSearchService
{
    private Logger logger = Logger.getLogger(StanbolContenthubFeaturedSearchServiceImpl.class);

    /**
     * Constructor
     * 
     * @param restClient REST Client
     */
    public StanbolContenthubFeaturedSearchServiceImpl(RestClient restClient)
    {
        super(restClient);
    }

    /**
     * @see org.apache.stanbol.client.services.StanbolContenthubService#featuredSearch(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public FeaturedSearchResult search(String index, String queryTerm, String solrQuery,
            Map<String, List<String>> facets, String ontologyURI, int offset, int limit) throws StanbolServiceException
    {
        final String featuredSearchUrl = StanbolContenthubStoreService.STANBOL_CONTENTHUB_PATH + index
                + StanbolContenthubStoreService.STANBOL_CONTENTHUB_SEARCH_PATH;

        Parameters par = new Parameters();
        if (queryTerm != null)
            par.put("queryTerm", queryTerm);
        if (solrQuery != null)
            par.put("solrQuery", solrQuery);
        if (ontologyURI != null)
            par.put("ontologyURI", ontologyURI);
        if (facets != null)
        {
            try
            {
                par.put("constraints", facetsToJSON(facets));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException(
                        "Impossible to stringify to JSON representation the facet constrains map " + facets.toString());
            }
        }

        par.put("offset", "" + offset);
        if (limit > 0)
            par.put("limit", "" + limit);

        ClientResponse response = getRestClient().get(featuredSearchUrl, MediaType.APPLICATION_JSON_TYPE, par);
        int status = response.getStatus();

        // Bad Request
        if (status == 400)
        {
            throw new StanbolServiceException(
                    "An error has been produced trying to perform a featured search through Stanbol ContentHub Index "
                            + index + " .BAD REQUEST: " + response.getClientResponseStatus().getReasonPhrase());
        }

        // Check HTTP status code
        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error trying to perform a featured search through Stanbol ContentHub Index " + index);
        }

        // Unmarshall Response
        JSONObject responseObject = response.getEntity(JSONObject.class);

        if (logger.isDebugEnabled())
            logger.debug("ContentHub FeaturedSearch Response: " + responseObject.toString());

        return SearchResultParser.parse(index, responseObject, new StanbolContenthubSolrSearchServiceImpl(
                getRestClient()));
    }

    private String facetsToJSON(Map<String, List<String>> facets) throws JSONException
    {
        JSONObject result = new JSONObject();
        for (Entry<String, List<String>> entry : facets.entrySet())
        {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            JSONArray array = new JSONArray();
            for (String facetValue : values)
                array.put(facetValue);

            result.put(name, array);
        }

        return result.toString();
    }

    /**
     * @see org.apache.stanbol.client.services.StanbolContenthubService#solrSearch(java.lang.String, java.lang.String)
     */
    @Override
    public FeaturedSearchResult solrSearch(String index, String query) throws StanbolServiceException
    {
        return search(index, null, query, null, null, 0, 0);
    }

    
    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService#solrSearch(java.lang.String, org.apache.solr.client.solrj.SolrQuery)
     */
    @Override
    public FeaturedSearchResult solrSearch(String index, SolrQuery query) throws StanbolServiceException
    {
        return search(index, null, query.toString(), null, null, 0, 0);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService#facetedSearch(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public FeaturedSearchResult facetedSearch(String index, String query, List<String> facetFields)
            throws StanbolServiceException
    {
        FeaturedSearchResult result = search(index, query, null, null, null, 0, 0);
        Iterator<FacetResult> iterator = result.getFacetResults().iterator();
        while (iterator.hasNext())
            if (!facetFields.contains(iterator.next().getFacetField().getName()))
                iterator.remove();

        return result;
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService#facetedSearch(java.lang.String, org.apache.solr.client.solrj.SolrQuery, java.util.List)
     */
    @Override
    public FeaturedSearchResult facetedSearch(String index, SolrQuery query, List<String> facetFields)
            throws StanbolServiceException
    {
        for (String facetField : facetFields)
            query.addFacetField(facetField);

        return solrSearch(index, query);
    }

    /**
     * @see org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService#facetedSearch(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public FeaturedSearchResult facetedSearch(String index, String query, Map<String, List<String>> constraints)
            throws StanbolServiceException
    {
        return search(index, query, null, constraints, null, 0, 0);
    }
}