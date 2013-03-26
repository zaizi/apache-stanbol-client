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

import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Represents an annotation in the FISE ontology
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class Annotation extends Enhancement implements Comparable<Annotation>
{

    // properties
    private final String extractedFrom; // http://fise.iks-project.eu/ontology/extracted-from
    private final Double confidence; // http://fise.iks-project.eu/ontology/confidence

    /**
     * Constructor
     * 
     * @param resource Jena resource
     */
    public Annotation(Resource resource)
    {
        super(resource);
        this.extractedFrom = resource.hasProperty(FISE.EXTRACTED_FROM) ? resource.getPropertyResourceValue(FISE.EXTRACTED_FROM).getURI() : null;
        this.confidence = resource.hasProperty(FISE.CONFIDENCE) ? resource.getProperty(FISE.CONFIDENCE).getDouble() : null;
    }

    /**
     * Get the fise:extracted-from property
     * 
     * @return fise:extracted-from property
     */
    public String getExtractedFrom()
    {
        return extractedFrom;
    }

    /**
     * Get the fise:confidence property
     * 
     * @return fise:confidence property
     */
    public Double getConfidence()
    {
        return confidence;
    }

    @Override
    public int compareTo(Annotation o)
    {
        if(this.equals(o))
            return 0;
        
        if(this.confidence > o.getConfidence())
            return -1;
        else 
            return 1;
    }
}
