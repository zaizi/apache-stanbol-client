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
package org.apache.stanbol.client.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.stanbol.client.model.parser.EnhancementParser;
import org.apache.stanbol.client.model.parser.EntityParser;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Represent a content item
 * 
 * @author efoncubierta
 * 
 */
public class ContentItem {

    private Model model;
    private String id;
    private Map<String, Enhancement> enhancements;
    private Map<String, Entity> entities;

    /**
     * Constructor
     * 
     * @param id
     *            Content id
     * @param enhancements
     *            Content enhancements
     */
    public ContentItem(String id, Model model) {
        this.id = id;
        this.model = model;
    }

    /**
     * Get the content id
     * 
     * @return Content id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the content enhancements
     * 
     * @return Content enhancements
     */
    public Map<String, Enhancement> getEnhancements() {
        if (enhancements == null) {
            enhancements = new HashMap<String, Enhancement>();
            for (Enhancement enhancement : EnhancementParser.parse(model)) {
                enhancements.put(enhancement.getUri(), enhancement);
            }
        }
        return enhancements;
    }

    /**
     * Get the entities related to the content
     * 
     * @return Entities related
     */
    public Map<String, Entity> getEntities() {
        if (entities == null) {
            entities = new HashMap<String, Entity>();
            for (Entity entity : EntityParser.parse(model)) {
                entities.put(entity.getUri(), entity);
            }
        }
        return entities;
    }
}
