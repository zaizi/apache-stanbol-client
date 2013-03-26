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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.response.FacetField;


/**
 * Abstract class that encapsulates the common information for any kind of Search within Stanbol ContentHub. It defines the minimum structure of a Search Result returned for any
 * Search Service in Stanbol. Each implemented search service should extend this class and take care of initialize the result data: a List of retrieved {@link DocumentResult}s 
 * from the ContentHub and a List of {@link FacetResult}s for the obtained resultant Documents ready to use in subsequent Faceted Search queries. Each {@link DocumentResult} corresponds
 * to a ContentItem from a specific index within the ContentHub
 * 
 * @author Rafa Haro
 *
 */
public abstract class StanbolSearchResult
{
    /**
     * ContentHub Index (Solr Core)
     */
    protected String indexName;
    
    /**
     * List of returned Documents from the ContentHub's index identified by indexName
     */
    protected List<DocumentResult> itemResults;
    
    /**
     * List of facets for the returned Documents
     */
    protected List<FacetResult> facetResults;
    
    /**
     * Add a new {@link DocumentResult} to the Results List
     * 
     * @param item Document Result
     */
    public void addDocumentResult(DocumentResult item)
    {
        itemResults.add(item);
    }

    /**
     * Return the list of resultant documents for the performed Search operation. Each resultant document
     * corresponds to a ContentItem within an index of the ContentHub
     * 
     * @return {@link List} of a Search Service resultant {@link DocumentResult}
     */
    public List<DocumentResult> getItemResults()
    {
        return itemResults;
    }

    /**
     * Set the list of resultant documents for the performed Search Operation
     * 
     * @param itemResults {@link List} of a Search Service resultant {@link DocumentResult}
     */
    public void setItemResults(List<DocumentResult> itemResults)
    {
        this.itemResults = itemResults;
    }

    /**
     * Get ContentHub's index name where the search was performed. An index within ContentHub corresponds to a Solr Core
     * 
     * @return
     */
    public String getIndexName()
    {
        return indexName;
    }

    /**
     * Set ContentHub's Search Index
     * 
     * @param indexName Name of the index within ContentHub
     */
    public void setIndexName(String indexName)
    {
        this.indexName = indexName;
    }
    
    /**
     * Return the list of {@link FacetResult} associated to the resultant list of documents
     * 
     * @return {@link List} of {@link FacetResult}
     */
    public List<FacetResult> getFacetResults()
    {
        return facetResults;
    }

    /**
     * Set the list of {@link FacetResult} associated to the resultant list of documents
     * 
     * @param facetResults {@link List} of {@link FacetResult}
     */
    public void setFacetResults(List<FacetResult> facetResults)
    {
        this.facetResults = facetResults;
    }
    
    /**
     * Return a {@link List} of {@link DocumentResult} where each associated Solr Document has the value passed by parameter
     * for a field named as the fieldName passed by parameter
     * 
     * @param fieldName Solr Field Name
     * @param value Solr Field Value
     * @return {@link List} of {@link DocumentResult} obtained by filtering the resultant list of documents by a Solr Field Value
     */
    public List<DocumentResult> getDocumentsByFieldValue(String fieldName, String value)
    {
        List<DocumentResult> result = new ArrayList<DocumentResult>();
        for(DocumentResult document: itemResults)
            if(document.getFieldValues(fieldName).contains(value))
                result.add(document);
        
        return result;
    }

    /**
     * Return a {@link List} of {@link DocumentResult} where each associated Solr Document has, for each 
     * Field Name passed by parameter in the Map as keys, the list of values passed as values of the Map 
     * 
     * @param values {@link Map} with a list of String values for each Solr Field Name 
     * @return {@link List} of {@link DocumentResult} obtained by filtering the resultant list of documents by the Map
     */
    public List<DocumentResult> getDocumentByFieldValues(Map<String, List<String>> values)
    {
        List<DocumentResult> result = new ArrayList<DocumentResult>();
        for(DocumentResult document:itemResults){
            Iterator<Entry<String, List<String>>> it = values.entrySet().iterator();
            boolean containsAll = true;

            while(it.hasNext() && containsAll){
                Entry<String, List<String>> entry = it.next();                
                if(!document.getFieldValues(entry.getKey()).containsAll(entry.getValue()))
                    containsAll = false;
            }

            if(containsAll)
                result.add(document);
        }

        return result;
    }
    
    /**
     * Return a {@link FacetResult} from the list of resultant facets by its name
     * 
     * @param facetName {@link FacetField} Name
     * @return {@link FacetResult} for the facetName
     */
    public FacetResult getFacetResult(String facetName){
        FacetResult result = null;
        for(int i = 0; i < facetResults.size() && result == null;i++)
            if(facetResults.get(i).getFacetField().getName().equals(facetName))
                result = facetResults.get(i);
        
        return result;
        
    }
}