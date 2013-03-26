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
package org.apache.stanbol.client.contenthub.store.model;

import java.util.List;

/**
 * Default Metadata Structure Implementation used in ContentHub ContentItem's retrieval service to build the Metadata from 
 * an RDF Graph 
 * 
 * @author Rafa Haro
 *
 */
public class DefaultMetadata extends Metadata
{
    /**
     * Constructor
     * 
     * @param name Metadata's Name
     * @param value Metadata's values
     * @param namespace Metadata's namespace
     * @param type Metadata's type
     * @param creator Metadata's creator
     * @param URI Metadata's URI
     */
    public DefaultMetadata(String name, List<String> value, String namespace, String type, String creator, String URI)
    {
        this.name = name;
        this.value = value;
        this.namespace = namespace;
        this.type = type;
        this.creator = creator;
        this.URI = URI;
    }
}
