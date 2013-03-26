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
package org.apache.stanbol.client.contenthub.store.services;

import java.net.URI;
import java.util.Map;

import org.apache.stanbol.client.contenthub.store.model.ContentHubDocumentRequest;
import org.apache.stanbol.client.contenthub.store.model.ContentItem;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.services.StanbolService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol ContentHub Store
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public interface StanbolContenthubStoreService extends StanbolService
{

    // TODO Homogenize PATHs

    // Stanbol Contenthub services URLs
    static final String STANBOL_CONTENTHUB_PATH = "contenthub/";
    static final String STANBOL_CONTENTHUB_SEARCH_PATH = "/search/featured";
    static final String STANBOL_CONTENTHUB_LDPATH_PATH = "ldpath";
    static final String STANBOL_CONTENTHUB_STORE_PATH = "/store";
    static final String STANBOL_CONTENTHUB_CONTENT_PATH = "content/";
    static final String STANBOL_CONTENTHUB_METADATA_PATH = "/metadata/";
    static final String STANBOL_CONTENTHUB_RAW_PATH = "/raw/";

    public static final String STANBOL_DEFAULT_INDEX = "contenthub";

    /**
     * Create a ContentItem
     * 
     * @param index ContentHub index where the Document will be stored
     * @param enhancementChain name of the enhancement chain that will be used to enrich the content
     * @param request Content hub request
     * @return Content URI location
     * @throws StanbolServiceException
     */
    public String add(String index, String enhancementChain, ContentHubDocumentRequest request)
            throws StanbolServiceException;

    /**
     * Delete a ContentItem
     * 
     * @param index Contenthub index
     * @param id Content ID
     * @return Content URI location
     * @throws StanbolServiceException
     */
    public void delete(String index, String id) throws StanbolServiceException;

    /**
     * Get ContentItem from Stanbol ContentHub's index
     * 
     * @param index Contenthub index
     * @param id Content ID
     * @return Content item
     * @throws StanbolServiceException
     */
    public ContentItem get(String index, String uri, boolean downloadContent) throws StanbolServiceException;

    /**
     * Create a new Index into ContentHub using LDPathProgram for Semantic Indexing
     * 
     * @param name Index Name
     * @param program LDPath Program
     * @throws StanbolServiceException
     */
    public URI createIndex(String name, LDPathProgram program) throws StanbolServiceException;

    /**
     * Delete a ContentHub index (LDPathProgram) by name
     * 
     * @param name The name of the index
     * @throws StanbolServiceException
     */
    public void deleteIndex(String name) throws StanbolServiceException;

    /**
     * Get All indexes managed by ContentHub and their names
     * 
     * @return Map <IndexName, LDPathProgram>
     */
    public Map<String, LDPathProgram> getIndexes() throws StanbolServiceException;

}