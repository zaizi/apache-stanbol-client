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
package org.apache.stanbol.client.services;

import java.net.URI;
import java.util.Map;

import org.apache.stanbol.client.model.ContentHubDocument;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.model.LDPathProgram;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer
 * 
 * @author efoncubierta, rharo
 *
 */
public interface StanbolContenthubService extends StanbolService {
	
	// Stanbol Contenthub services URLs
	public static final String STANBOL_CONTENTHUB_PATH = "contenthub/";
	
	public static final String STANBOL_CONTENTHUB_STORE_PATH = "/store/";
    public static final String STANBOL_CONTENTHUB_CONTENT_PATH = "content/";
    public static final String STANBOL_CONTENTHUB_METADATA_PATH = "metadata/";
    
	public static final String STANBOL_DEFAULT_INDEX = "contenthub";
	
	/**
	 * Create a content
	 * 
	 * @param request Content hub request
	 * @return Content URI location
	 * @throws StanbolServiceException
	 */
	public URI add(ContentHubDocument request) throws StanbolServiceException;
	
    /**
     * Delete a content
     * 
     * @param index Contenthub index
     * @param id Content ID
     * @return Content URI location
     * @throws StanbolServiceException
     */
    public void delete(String index, String id) throws StanbolServiceException;
    
    /**
     * Delete a content
     * 
     * @param request Content hub request
     * @return Content URI location
     * @throws StanbolServiceException
     */
    public void delete(ContentHubDocument request) throws StanbolServiceException;

	/**
	 * Get data information of a content
	 * 
	 * @param index Contenthub index
	 * @param id Content ID
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem get(String index, String id) throws StanbolServiceException;
	
    /**
     * Get data information of a content
     * 
     * @param request Content hub request
     * @return Content item
     * @throws StanbolServiceException
     */
    public ContentItem get(ContentHubDocument request) throws StanbolServiceException;
    
    /**
     * Create a new Index into ContentHub using LDPathProgram for Semantic Indexing
     * 
     * @param name Index Name
     * @param program LDPath Program
     */
    public void createIndex(String name, LDPathProgram program);
    
    /**
     * Get All indexes managed by ContentHub and their names
     * 
     * @return Map <IndexName, LDPathProgram>
     */
    public Map<String, LDPathProgram> getIndexes();
    
    // TODO Include Methods for Related KeyWord Search --> http://stanbol.apache.org/docs/trunk/components/contenthub/contenthub5min
}
