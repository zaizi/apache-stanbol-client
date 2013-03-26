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
package org.apache.stanbol.client.contenthub.search.model;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Model the result of any Stanbol ContentHub Solr Based search. SolrSearchResult class is a specialization of {@link StanbolSearchResult}
 * that just includes the Solr Response {@link QueryResponse} object from SolrJ library 
 * 
 * @author Rafa Haro
 * 
 */
public class SolrSearchResult extends StanbolSearchResult
{
    
    private QueryResponse solrResponse;

    SolrSearchResult(String indexName, List<DocumentResult> itemResults, List<FacetResult> facetResults, QueryResponse solrResponse)
    {
        this.indexName = indexName;
        this.itemResults = itemResults;
        this.solrResponse = solrResponse;
        this.facetResults = facetResults;
    }

    /**
     * Get Solr Response associated to the Solr Based Search
     * 
     * @return {@link QueryResponse} object
     */
    public QueryResponse getSolrResponse()
    {
        return solrResponse;
    }

    /**
     * Set the associated Solr Response for the current Stanbol Search Result
     * 
     * @param solrResponse {@link QueryResponse} Solr Response
     */
    void setSolrResponse(QueryResponse solrResponse)
    {
        this.solrResponse = solrResponse;
    }

}
