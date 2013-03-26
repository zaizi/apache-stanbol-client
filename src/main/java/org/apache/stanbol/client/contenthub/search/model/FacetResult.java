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

import org.apache.solr.client.solrj.response.FacetField;

/**
 * Stanbol Facet Result, containing a Solr Facet Field and the type of the Facet. The type allows the user to know if 
 * Range Queries can be performed.
 * 
 * @author Rafa Haro
 *
 */
public class FacetResult
{

    private FacetField facetField;

    private String type;

    /**
     * Non-type Constructor
     * 
     * @param facetField Solr FacetField
     */
    FacetResult(FacetField facetField)
    {
        this.facetField = facetField;
        this.type = "UNKNOWN";
    }

    /**
     * Default Constructor
     * 
     * @param facetField Solr FacetField
     * @param type Facet type
     */
    FacetResult(FacetField facetField, String type)
    {
        this.facetField = facetField;
        this.type = type;
    }

    /**
     *  
     * @return Solr FacetField
     */
    public FacetField getFacetField()
    {
        return this.facetField;
    }

    /**
     * 
     * @return Facet type
     */
    public String getType()
    {
        return this.type;
    }
}
