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
package org.apache.stanbol.client.enhancer.impl;

import java.io.InputStream;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.stanbol.client.Enhancer;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Collect all the parameters that can be sent to the enhancer in order to configure both the type of enrichment and the response
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 *
 */
public class EnhancerParameters {
	
	/**
     * Accepted Output Formats
     */
    public static enum OutputFormat {
        RDFXML(new MediaType("application", "rdf+xml")),
        TURTLE(new MediaType("text", "turtle")),
        NT(new MediaType("text", "rdf+n3"));


        private final MediaType type;

        private OutputFormat(MediaType type) {
            this.type = type;
        }

        public MediaType value() {
            return type;
        }

        public static OutputFormat get(String type) {
            for (OutputFormat of : OutputFormat.values())
                if (of.type.toString().equals(type))
                    return of;
            return null;
        }
    }
    
    private OutputFormat outputFormat = OutputFormat.TURTLE;
    private InputStream content;
    private String stringContent;
    private boolean contentSwitch = false; // False -> Stream, True -> Content
    private String chain = Enhancer.DEFAULT_CHAIN;
    private Collection<String> dereferencedFields = Sets.newHashSet();
    private Optional<String> ldpath = Optional.absent();
    
    public MediaType getOutputFormat(){
    	return outputFormat.value();
    }
    
    public InputStream getContent(){
    	if(!contentSwitch)
    		return content;
    	else
    		return IOUtils.toInputStream(stringContent);
    }
    
    public String getChain(){
    	return chain;
    }
    
    public Collection<String> getDereferencedFields(){
    	return dereferencedFields;
    }
    
    public String getLdPath(){
    	return ldpath.orNull();
    }
    
    public static class EnhancerParametersBuilder {
    	private final EnhancerParameters parameters = new EnhancerParameters();
    	
    	public EnhancerParametersBuilder setOutputFormat(OutputFormat format){
    		this.parameters.outputFormat = format;
    		return this;
    	}
    	
    	public EnhancerParametersBuilder setContent(String content){
    		this.parameters.contentSwitch = true;
    		this.parameters.stringContent = content;
    		return this;
    	}
    	
    	public EnhancerParametersBuilder setContent(InputStream content){
    		this.parameters.contentSwitch = false;
    		this.parameters.content = content;
    		return this;
    	}
    	
    	public EnhancerParametersBuilder setChain(String chain){
    		this.parameters.chain = chain;
    		return this;
    	}
    	
    	public EnhancerParametersBuilder setLDpathProgram(String ldpathProgram){
        	this.parameters.ldpath = Optional.of(ldpathProgram);
        	return this;
        }
        
        public EnhancerParametersBuilder addDereferencingField(String field){
        	this.parameters.dereferencedFields.add(field);
        	return this;
        }
    	
    	public EnhancerParameters buildDefault(String content){
    		EnhancerParameters params = new EnhancerParameters();
    		params.contentSwitch = true;
    		params.stringContent = content;
    		return params;
    	}
    	
    	public EnhancerParameters build(){
    		return parameters;
    	}
    }
    
    /**
     * Create a new Enhancer Parameters Builder
     *
     * @return Created {@link EnhancerParametersBuilder}
     */
    public static EnhancerParametersBuilder builder() {
        return new EnhancerParametersBuilder();
    }
}
