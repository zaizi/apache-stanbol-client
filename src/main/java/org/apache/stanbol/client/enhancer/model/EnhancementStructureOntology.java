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
package org.apache.stanbol.client.enhancer.model;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

/**
 * FISE ontology definitions
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class EnhancementStructureOntology
{

    public static final String FISE_URI = "http://fise.iks-project.eu/ontology/";
    public static final String ENTITYHUB_URI = "http://stanbol.apache.org/ontology/entityhub/entityhub#";

    public static final Resource ENHANCEMENT = new ResourceImpl(FISE_URI, "Enhancement");
    public static final Resource ENTITY_ANNOTATION = new ResourceImpl(FISE_URI, "EntityAnnotation");
    public static final Resource TEXT_ANNOTATION = new ResourceImpl(FISE_URI, "TextAnnotation");
    public static final Resource USER_ANNOTATION = new ResourceImpl(FISE_URI, "UserAnnotation");

    public static final Property EXTRACTED_FROM = new PropertyImpl(FISE_URI + "extracted-from");
    public static final Property CONFIDENCE = new PropertyImpl(FISE_URI + "confidence");

    public static final Property ENTITY_LABEL = new PropertyImpl(FISE_URI + "entity-label");
    public static final Property ENTITY_REFERENCE = new PropertyImpl(FISE_URI + "entity-reference");
    public static final Property ENTITY_TYPE = new PropertyImpl(FISE_URI + "entity-type");
    public static final Property ENTITYHUB_SITE = new PropertyImpl(ENTITYHUB_URI + "site");

    public static final Property SELECTED_TEXT = new PropertyImpl(FISE_URI + "selected-text");
    public static final Property SELECTION_CONTEXT = new PropertyImpl(FISE_URI + "selection-context");
    public static final Property START = new PropertyImpl(FISE_URI + "start");
    public static final Property END = new PropertyImpl(FISE_URI + "end");
}
