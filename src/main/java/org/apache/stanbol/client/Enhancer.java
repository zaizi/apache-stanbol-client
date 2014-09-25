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
package org.apache.stanbol.client;

import org.apache.stanbol.client.enhancer.impl.EnhancerParameters;
import org.apache.stanbol.client.enhancer.model.EnhancementStructure;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer according to its REST API
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 * 
 */
public interface Enhancer
{

    // Stanbol Enhancer service urls
    public static final String STANBOL_ENHANCER_PATH = "enhancer";
    public static final String STANBOL_CHAIN_PATH = "chain";
    public static final String DEFAULT_CHAIN = "default";
    
    
    /**
     * Enhance an {@link String} content with the settings specified as parameters
     * 
     * @param content Content to be enhanced
     * @param parameters Enhancer parameters
     * @return {@link EnhancementStructure} containing all the semantic metadata extracted from the content
     * @throws StanbolServiceException
     */
    public EnhancementStructure enhance(EnhancerParameters parameters) throws StanbolServiceException;
}
