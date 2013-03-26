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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model the result of Stanbol FeaturedSearch service. FeaturedSearchResult class is a specialization of {@link StanbolSearchResult} 
 * that just includes Search Related Keywords obtained from the result of FeaturedSearch service.   
 * 
 * @author Rafa Haro
 * 
 */
public class FeaturedSearchResult extends StanbolSearchResult
{

    private Map<String, Map<String, List<RelatedKeyword>>> relatedKeywords;

    FeaturedSearchResult(String indexName, List<DocumentResult> itemResults, List<FacetResult> facetResults,
            Map<String, Map<String, List<RelatedKeyword>>> relatedKeywords)
    {
        this.indexName = indexName;
        this.itemResults = itemResults;
        this.facetResults = facetResults;
        this.relatedKeywords = relatedKeywords;
    }

    /**
     * Add a new {@link RelatedKeyword} to the FeaturedSearch Result
     * 
     * @param relatedTo Search Keyword String which has Related Keywords  
     * @param source Related Keywords Resource source
     * @param keyword New {@link RelatedKeyword}
     */
    public void addRelatedKeyword(String relatedTo, String source, RelatedKeyword keyword)
    {
        Map<String, List<RelatedKeyword>> keywords = relatedKeywords.get(relatedTo);
        if (keywords != null)
        {
            List<RelatedKeyword> listKeywords = keywords.get(source);
            if (listKeywords == null)
                keywords.put(source, Arrays.asList(keyword));
            else
                listKeywords.add(keyword);
        }
        else
        {
            keywords = new HashMap<String, List<RelatedKeyword>>();
            keywords.put(source, Arrays.asList(keyword));
            relatedKeywords.put(relatedTo, keywords);

        }
    }

    /**
     * Get a List of {@link RelatedKeyword} for a given Source and Keyword
     * 
     * @param source Related Keyword's resource's source
     * @param relatedTo Initial Search Keyword
     * @return List of {@link RelatedKeyword} for the given Source and Search Keyword
     */
    public List<RelatedKeyword> getRelatedKeywords(String source, String relatedTo)
    {
        return relatedKeywords.get(source) != null ? relatedKeywords.get(source).get(relatedTo) : null;
    }

    /**
     * Get all {@link RelatedKeyword} associated to the Search Result by initial keyword and source
     * 
     * @return Data Structure with {@link RelatedKeyword} by Source and Initial Search Keyword
     */
    public Map<String, Map<String, List<RelatedKeyword>>> getRelatedKeywords()
    {
        return relatedKeywords;
    }

    /**
     * Set all {@link RelatedKeyword} associated to the Search Result by initial keyword and source
     * 
     * @param relatedKeywords Related Keywords by search keyword and source
     */
    public void setRelatedKeywords(Map<String, Map<String, List<RelatedKeyword>>> relatedKeywords)
    {
        this.relatedKeywords = relatedKeywords;
    }

}