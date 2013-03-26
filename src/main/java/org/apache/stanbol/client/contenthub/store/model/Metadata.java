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
 * This abstract class is a skeleton with the common attributes that any Metadata structure should have to be stored in the ContentHub
 * This class should be extended and decorated to easily build Stanbol Client Metadata structure from Metadata objects in the 
 * context where the Stanbol Client is being integrated
 * 
 * @author Rafa Haro
 * 
 */
public abstract class Metadata
{
    /**
     * Metadata's type
     */
    protected String type = null;

    /**
     * Metadata's name
     */
    protected String name = null;

    /**
     * Metadata's value
     */
    protected List<String> value = null;

    /**
     * Metadata's URI
     */
    protected String URI = null;
    
    /**
     * Metadata's namespace
     */
    protected String namespace = null;

    /**
     * Metadata's creator
     */
    protected String creator = null;

    /**
     * Getter for Metadata Type
     * 
     * @return Metadata's Type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Getter for Metadata Name
     * 
     * @return Metadata's Name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter for Metadata Values
     * 
     * @return Metadata's Values
     */
    public List<String> getValue()
    {
        return value;
    }

    /**
     * Getter for Metadata URI
     * 
     * @return Metadata's URI
     */
    public String getURI()
    {
        return URI;
    }

    /**
     * Getter for Metadata Namespace
     * 
     * @return Metadata's Namespace
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Getter for Metadata Creator
     * 
     * @return Metadata's Creator
     */
    public String getCreator()
    {
        return creator;
    }
}
