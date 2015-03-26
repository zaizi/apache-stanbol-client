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
package org.apache.stanbol.client.entityhub.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.stanbol.client.exception.StanbolClientException;

/**
 * Represents an LDPath Program. The purpose of this class is to try to have a friendly way to programatically build LDPath programs in order
 * to ease the integration of the Stanbol Client in other applications or frameworks. Currently, it supports the definitions of Namespaces
 * and fields and the parsing of LDPath programs in String formats 
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 * 
 */
public class LDPathProgram
{
    /* Static RegExp Parsers */
    private static final Pattern prefixPattern = Pattern.compile("@prefix\\s*(\\S*)((\\s*:\\s*)|(:\\s*))<(\\S*)>(\\s*)?;");
    private static final Pattern fieldPattern = Pattern.compile("(\\S*)\\s*=(([^;]*));");

    /*
     * Map of Prefix - Namespace
     */
    private Map<String, String> namespaces;

    /*
     * Map of LDPathField - Field Definition
     */
    private Map<LDPathField, String> fields;

    /**
     * Default Constructor
     */
    public LDPathProgram()
    {
        namespaces = new HashMap<String, String>();
        fields = new HashMap<LDPathField, String>();
    }

    /**
     * Parsing Constructor
     * 
     * @param ldPathProgram LDPath Program String
     * @throws StanbolClientException
     */
    public LDPathProgram(String ldPathProgram) throws StanbolClientException
    {
        this();

        // Parameter Parsing;
        Matcher matcher = prefixPattern.matcher(ldPathProgram);
        while (matcher.find())
        {
            String prefix = matcher.group(1);
            if (prefix == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Prefix Definition Error");

            String namespace = matcher.group(5);
            if (namespace == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Namespace Definition Error");

            namespaces.put(prefix, namespace);
        }

        String restProgram = ldPathProgram;
        if (namespaces.size() > 0)
        {
            int prefix = ldPathProgram.lastIndexOf("@prefix");
            String rest = ldPathProgram.substring(prefix);
            restProgram = rest.substring(rest.indexOf(';') + 1);
        }

        matcher = fieldPattern.matcher(restProgram);

        while (matcher.find())
        {
            String fieldName = matcher.group(1);
            if (fieldName == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Field Name Definition Error");

            String fieldDefinition = matcher.group(2);
            if (fieldDefinition == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Field Definition Error");

            int qIndex = fieldName.indexOf(':');
            if (qIndex != -1)
            {
                String fieldPrefix = fieldName.substring(0, qIndex);
                if (namespaces.get(fieldPrefix) == null)
                    throw new StanbolClientException(
                            "LDPath Program Sintax Error. Field Name Prefix doesn't exist as Namespace Prefix");
                String fieldValue = fieldName.substring(qIndex + 1, fieldName.length());

                fields.put(new LDPathField(fieldPrefix, fieldValue), fieldDefinition);
            }
            else
                fields.put(new LDPathField(fieldName), fieldDefinition);
        }
    }

    /**
     * Add a New Namespace Definition to the LDPath Program
     * 
     * @param prefix Namespace Prefix
     * @param namespace Namespace URI
     */
    public void addNamespace(String prefix, String namespace)
    {
        if (!namespaces.containsValue(namespace))
            namespaces.put(prefix, namespace);
    }

    /**
     * Add a new Field Definition to the LDPath Program
     * 
     * @param fieldName Field Name without prefix
     * @param fieldDefinition Field Definition
     */
    public void addFieldDefinition(String fieldName, String fieldDefinition)
    {
        fields.put(new LDPathField(fieldName), fieldDefinition);
    }

    /**
     * Add a new Field Definition to the LDPath Program
     * 
     * @param fieldPrefix Field Prefix
     * @param fieldName Field Name
     * @param fieldDefinition Field Definition
     * @throws StanbolClientException
     */
    public void addFieldDefinition(String fieldPrefix, String fieldName, String fieldDefinition)
            throws StanbolClientException
    {
        if (namespaces.get(fieldPrefix) == null)
            throw new StanbolClientException(
                    "LDPath Program Sintax Error. Field Name Prefix doesn't exist as Namespace Prefix");

        fields.put(new LDPathField(fieldPrefix, fieldName), fieldDefinition);
    }

    /**
     * Get Namespace Definition by its Prefix
     * 
     * @param prefix Namespace's Prefix
     * @return Namespace Definition
     */
    public String getNamespace(String prefix)
    {
        return namespaces.get(prefix);
    }

    /**
     * Get Field Definition Definition by its name
     * 
     * @param fieldName Field Name
     * @return Field Definition
     */
    public String getFieldDefinition(String fieldName)
    {
        return fields.get(fieldName);
    }

    /**
     * Get Field Definition Definition by its prefix and name
     * 
     * @param prefix Field Prefix
     * @param fieldName Field Name
     * @return Field Definition
     */
    public String getFieldDefinition(String prefix, String fieldName)
    {
        LDPathField field = new LDPathField(prefix, fieldName);
        return fields.get(field);
    }

    /**
     * Get Prefix by its associated namespace definition
     * 
     * @param namespace Namespace's URI
     * @return Namespace's Prefix
     */
    public String getPrefix(String namespace)
    {
        Iterator<Entry<String, String>> it = namespaces.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, String> next = it.next();
            if (next.getValue().equals(namespace))
                return next.getKey();
        }

        return null;
    }

    /**
     * LDPath Program String representation 
     */
    public String toString()
    {
        String result = new String();

        // Prefixes
        Iterator<String> it = namespaces.keySet().iterator();
        while (it.hasNext())
        {
            String nextPrefix = it.next();
            result += "@prefix " + nextPrefix + ":<" + namespaces.get(nextPrefix) + ">;";
        }

        Iterator<LDPathField> itF = fields.keySet().iterator();
        while (itF.hasNext())
        {
            LDPathField nextField = itF.next();
            result += nextField.toString() + " = " + fields.get(nextField);
            if (!fields.get(nextField).endsWith(";"))
                result += ";";
        }

        return result;
    }

    private class LDPathField
    {

        private String prefix = null;

        private String name = null;

        public LDPathField(String prefix, String name)
        {
            this.prefix = prefix;
            this.name = name;
        }

        public LDPathField(String name)
        {
            this.name = name;
        }

        public boolean equals(Object obj)
        {
            LDPathField field = (LDPathField) obj;
            if (field.prefix == this.prefix && field.name == this.name)
                return true;
            else
                return false;
        }

        public String toString()
        {
            if (prefix != null)
                return prefix + ":" + name;
            else
                return name;
        }

    }

}
