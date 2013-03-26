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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.stanbol.client.contenthub.search.model.StanbolSolrVocabulary.SolrFieldName;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class contains a number of utility methods for parsing both Solr Search and FeaturedSearch service responses to {@link SolrSearchResult} and {@link FeaturedSearchResult} respectively
 * Also contains methods to transform {@link SolrDocument} to {@link DocumentResult} that it's the unit representation of a Search Result in the Stanbol Client   
 * 
 * @author Rafa Haro
 * 
 */
public class SearchResultParser
{
    /**
     * Converts a {@link SolrDocument} to a {@link DocumentResult}. DocumentResult attributes are filled using Solr Fields 
     * 
     * @param solrDocument Solr Document
     * @param indexName Name of the Solr Index (ContentHub Index)
     * @return DocumentResult parsed from the passed Solr Document
     */
    public static DocumentResult solrDocument2DocumentResult(SolrDocument solrDocument, String indexName)
    {
        return solrDocument2DocumentResult(solrDocument, null, indexName);
    }

    /**
     * Converts a {@link SolrDocument} to a {@link DocumentResult}. DocumentResult attributes are filled using Solr Fields
     * 
     * @param solrDocument Solr Document
     * @param baseURI Document Base URI
     * @param indexName Name of the Solr Index (ContentHub Index)
     * @return {@link DocumentResult} parsed from the passed Solr Document
     */
    public static DocumentResult solrDocument2DocumentResult(SolrDocument solrDocument, String baseURI, String indexName)
    {
        String id = getStringValueFromSolrField(solrDocument, SolrFieldName.ID.toString());
        String mimeType = getStringValueFromSolrField(solrDocument, SolrFieldName.MIMETYPE.toString());
        String title = getStringValueFromSolrField(solrDocument, SolrFieldName.TITLE.toString());
        String dereferencableURI = baseURI != null ? (baseURI + "contenthub/" + indexName + "/store/content/" + id)
                : null;
        title = (title == null || title.trim().equals("") ? id : title);
        int enhancementCount = Integer.parseInt(getStringValueFromSolrField(solrDocument, SolrFieldName.ENHANCEMENTCOUNT.toString()));
        return new DocumentResult(id, dereferencableURI, mimeType, enhancementCount, title, solrDocument);
    }
    
    /**
     * Converts a {@link QueryResponse} from Solr to a {@link SolrSearchResult}
     * 
     * @param indexName Name of the Solr Index (ContentHub Index)
     * @param response Solr QueryResponse
     * @param server Solr Server
     * @param baseURI Document Base URI
     * @return {@link SolrSearchResult} parsed from the passed Solr QueryResponse
     */
    public static SolrSearchResult queryResponse2SolrSearchResult(String indexName, QueryResponse response, SolrServer server, String baseURI){
        
        List<DocumentResult> itemResults = new ArrayList<DocumentResult>();
        for(SolrDocument document:response.getResults())
          itemResults.add(SearchResultParser.solrDocument2DocumentResult(document, baseURI, indexName));
        
        List<FacetResult> facetResults = new ArrayList<FacetResult>();
        List<FacetField> facetFields = response.getFacetFields();
        List<FacetResult> allFacets = null;
        if(facetFields != null){
            try
            {
                allFacets = getAllFacetResults(getAllFacetFields(server));
            }
            catch (SolrServerException e)
            {
                // Nothing to do
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // Nothing to do
                e.printStackTrace();
            }

            if (allFacets == null) {
                for (FacetField facetField : facetFields) {
                    if (facetField.getValues() != null) {
                        facetResults.add(new FacetResult(facetField));
                    }
                }
            } else {
                for (FacetField facetField : facetFields) {
                    if (facetField.getValues() != null) {
                        for (FacetResult facetResult : allFacets) {
                            if (facetResult.getFacetField().getName().equals(facetField.getName())) {
                                facetResults.add(new FacetResult(facetField, facetResult.getType()));
                            }
                        }
                    }
                }
            }
        }
        return new SolrSearchResult(indexName, itemResults, facetResults, response);
    }

    /**
     * Converts the JSON FeaturedSearch service response to a {@link FeaturedSearchResult} 
     * 
     * @param indexName Name of the Solr Index (ContentHub Index)
     * @param featuredSearchResponse JSON representation of Stanbol's FeaturedSearch service response
     * @return {@link FeaturedSearchResult}
     * @throws StanbolServiceException 
     */
    public static FeaturedSearchResult parse(String indexName, JSONObject featuredSearchResponse) throws StanbolServiceException
    {
        try
        {
            // Extract Documents
            JSONArray documents = featuredSearchResponse.getJSONArray("documents");
            List<DocumentResult> items = new ArrayList<DocumentResult>();
            for(int i = 0; i < documents.length();i++){
                JSONObject nextDocument = documents.getJSONObject(i);
                DocumentResult nextDocumentResult = new DocumentResult(nextDocument.getString("localid"), 
                        nextDocument.getString("mimetype"), 
                        nextDocument.getInt("enhancementcount"), 
                        nextDocument.getString("title"));
                items.add(nextDocumentResult);
            }
            
            // Extract Facets
            JSONArray facets = featuredSearchResponse.getJSONArray("facets");
            List<FacetResult> facetResults = new ArrayList<FacetResult>();
            for(int i = 0; i < facets.length(); i++){
                JSONObject nextFacet = facets.getJSONObject(i);
                JSONObject nextJSONFacetField = nextFacet.getJSONObject("facet");
                FacetField nextFacetField = new FacetField(nextJSONFacetField.getString("name"));
                
                JSONArray values = nextJSONFacetField.getJSONArray("values");
                for(int j = 0; j < values.length(); j++){
                    JSONObject value = values.getJSONObject(j);
                    nextFacetField.add(value.getString("name"), value.getLong("count"));
                }
                
                String type = nextFacet.getString("type");
                FacetResult nextFacetResult = new FacetResult(nextFacetField, type);
                facetResults.add(nextFacetResult);
            }
            
            // Extract 
            Map<String, Map<String, List<RelatedKeyword>>> relatedKeywords = new HashMap<String, Map<String, List<RelatedKeyword>>>();
            JSONArray relatedKeywordsArray = featuredSearchResponse.getJSONArray("relatedkeywords");
            for(int i = 0; i < relatedKeywordsArray.length(); i++){
                JSONObject nextRelatedKeyword = relatedKeywordsArray.getJSONObject(i);
             
                String nextKeyword = (String) nextRelatedKeyword.keys().next();
                JSONArray nextValues = nextRelatedKeyword.getJSONArray(nextKeyword);
                Map<String, List<RelatedKeyword>> nextGroup = new HashMap<String, List<RelatedKeyword>>();
                for(int j = 0;j < nextValues.length();j++){
                    JSONObject nextGroupKeywords = nextValues.getJSONObject(j);
                    @SuppressWarnings("rawtypes")
                    Iterator it = nextGroupKeywords.keys();
                    while(it.hasNext()){
                        String nextSource = (String) it.next();
                        List<RelatedKeyword> keywords = new ArrayList<RelatedKeyword>();
                        JSONArray keywordInfoArray = nextGroupKeywords.getJSONArray(nextSource);
                        for(int k = 0; k < keywordInfoArray.length(); k++){
                            JSONObject nextInfo = keywordInfoArray.getJSONObject(k);
                            RelatedKeyword rk = new RelatedKeyword(nextInfo.getString("keyword"),
                                    nextInfo.getDouble("score"),
                                    RelatedKeyword.Source.valueOf((nextSource)));
                            keywords.add(rk);
                        }
                        nextGroup.put(nextSource, keywords);
                    }
                }
                
                relatedKeywords.put(nextKeyword, nextGroup);
            }
            return new FeaturedSearchResult(indexName, items, facetResults, relatedKeywords);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new StanbolServiceException("Error parsing FeaturedSearch JSON response " + featuredSearchResponse.toString() + ". Error message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts the JSON FeaturedSearch service response to a {@link FeaturedSearchResult} adding associated {@link SolrDocument} to each {@link DocumentResult} 
     * 
     * @param indexName Name of the Solr Index (ContentHub Index)
     * @param featuredSearchResponse JSON representation of Stanbol's FeaturedSearch service response
     * @param searchService Stanbol Client ContentHub Solr Search Service instance
     * @return {@link FeaturedSearchResult}
     * @throws StanbolServiceException 
     */
    public static FeaturedSearchResult parse(String indexName, JSONObject featuredSearchResponse, StanbolContenthubSolrSearchService searchService) throws StanbolServiceException
    {
        FeaturedSearchResult result = parse(indexName, featuredSearchResponse);
        for(DocumentResult document:result.getItemResults())
        {
            String query = SolrFieldName.ID + ":" + ClientUtils.escapeQueryChars(document.getLocalId());
            SolrQuery solrQuery = new SolrQuery(query);
            
            SolrSearchResult response;
            try
            {
                response = searchService.search(result.getIndexName(), solrQuery);
            }
            catch (StanbolServiceException e)
            {
                e.printStackTrace();
                continue;
            }
            
            if(response.getSolrResponse().getResults().size() > 0)
                document.setSolrDocument(response.getSolrResponse().getResults().get(0));
        }
        return result;
    }

    static String getStringValueFromSolrField(SolrDocument solrDocument, String field)
    {
        Object result = solrDocument.getFieldValue(field);
        if (result != null)
        {
            return result.toString();
        }
        return "";
    }
    
    /**
     * Get a List of FacetField from a Solr Server
     * 
     * @param server Solr Server
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static NamedList<Object> getAllFacetFields(SolrServer server) throws SolrServerException, IOException
    {
        LukeRequest qr = new LukeRequest();
        NamedList<Object> qresp = server.request(qr);
        Object fields = qresp.get("fields");
        if (fields instanceof NamedList<?>)
        {
            @SuppressWarnings("unchecked")
            NamedList<Object> fieldsList = (NamedList<Object>) fields;
            return fieldsList;
        }
        else
        {
            throw new IllegalStateException(
                    "Fields container is not a NamedList, so there is no facet information available");
        }
    }

    /**
     * Get Solr Facet Field type
     * 
     * @param fieldName Solr Field Name
     * @param fieldList Solr Fields List
     * @return
     */
    public static String getFacetFieldType(String fieldName, NamedList<Object> fieldList)
    {
        for (int i = 0; i < fieldList.size(); i++)
        {
            if (fieldName.equals(fieldList.getName(i)))
            {
                @SuppressWarnings("unchecked")
                NamedList<Object> values = (NamedList<Object>) fieldList.getVal(i);
                return ((String) values.get("type")).trim();

            }
        }

        return null;
    }
    
    private static List<FacetResult> getAllFacetResults(NamedList<Object> fieldsList){

        List<FacetResult> facetResults = new ArrayList<FacetResult>();

        for (int i = 0; i < fieldsList.size(); i++) {
            String fn = fieldsList.getName(i);
            @SuppressWarnings("unchecked")
            NamedList<Object> values = (NamedList<Object>) fieldsList.getVal(i);
            String type = (String) values.get("type");
            facetResults.add(new FacetResult(new FacetField(fn), type.trim()));
        }
        return facetResults;
    }
}
