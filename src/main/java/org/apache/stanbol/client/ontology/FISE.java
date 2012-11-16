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
package org.apache.stanbol.client.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

/**
 * FISE ontology definitions
 * 
 * @author efoncubierta
 *
 */
public class FISE {

	public static final String URI = "http://fise.iks-project.eu/ontology/";
	
	public static final Resource ENHANCEMENT = new ResourceImpl(URI, "Enhancement");
	public static final Resource ENTITY_ANNOTATION = new ResourceImpl(URI, "EntityAnnotation");
	public static final Resource TEXT_ANNOTATION = new ResourceImpl(URI, "TextAnnotation");
	
	public static final Property EXTRACTED_FROM = new PropertyImpl(URI + "extracted-from");
	public static final Property CONFIDENCE = new PropertyImpl(URI + "confidence");
	
	public static final Property ENTITY_LABEL = new PropertyImpl(URI + "entity-label");
	public static final Property ENTITY_REFERENCE = new PropertyImpl(URI + "entity-reference");
	public static final Property ENTITY_TYPE = new PropertyImpl(URI + "entity-type");
	
	public static final Property SELECTED_TEXT = new PropertyImpl(URI + "selected-text");
	public static final Property SELECTION_CONTEXT = new PropertyImpl(URI + "selection-context");
	public static final Property START = new PropertyImpl(URI + "start");
	public static final Property END = new PropertyImpl(URI + "end");
}
