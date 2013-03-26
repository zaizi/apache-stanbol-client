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
package org.apache.stanbol.client.enhancer.services;

import java.io.File;
import java.io.InputStream;

import org.apache.stanbol.client.enhancer.model.EnhancementResult;
import org.apache.stanbol.client.services.StanbolService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer
 * 
 * @author efoncubierta
 * 
 */
public interface StanbolEnhancerService extends StanbolService
{

    // Stanbol Enhancer service url
    public static final String STANBOL_ENHANCER_PATH = "enhancer";
    public static final String STANBOL_CHAIN_PATH = "chain";
    public static final String DEFAULT_CHAIN = "default";
    
    /**
     * Enhance a Content
     * 
     * @param URI Content URI
     * @param content Content String
     * @return List of enhancements
     * @throws StanbolServiceException 
     */
    public EnhancementResult enhance(String URI, String content) throws StanbolServiceException;
    
    /**
     * Enhance a Content
     * 
     * @param URI Content URI
     * @param content Content String
     * @param chain Enhancement Chain Name
     * @return
     * @throws StanbolServiceException 
     */
    public EnhancementResult enhance(String URI, String content, String chain) throws StanbolServiceException;

    /**
     * Enhance a content
     * 
     * @param file File
     * @return List of enhancements
     * @throws StanbolServiceException
     */
    public EnhancementResult enhance(String URI, File file) throws StanbolServiceException;

    /**
     * Enhance a content
     * 
     * @param file File
     * @param chain Enhancement chain
     * @return List of enhancements
     * @throws StanbolServiceException
     */
    public EnhancementResult enhance(String URI, File file, String chain) throws StanbolServiceException;

    /**
     * Enhance a content
     * 
     * @param is InputStream
     * @return List of enhancements
     * @throws StanbolServiceException
     */
    public EnhancementResult enhance(String URI, InputStream is) throws StanbolServiceException;

    /**
     * Enhance a content
     * 
     * @param is InputStream
     * @param chain Enhancement chain
     * @return List of enhancements
     * @throws StanbolServiceException
     */
    public EnhancementResult enhance(String URI, InputStream is, String chain) throws StanbolServiceException;
}
