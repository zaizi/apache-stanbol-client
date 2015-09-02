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
package org.apache.stanbol.client.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.apache.stanbol.client.enhancer.model.EnhancementStructure.EnhancementStructureReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * RestEasy based implementation REST operations
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 *
 */
public class RestClientExecutor {
	
	private static final int TIMEOUT = 60;
	
	private static Client builder;
	
	static{
		ClientConfig cc = new DefaultClientConfig();
		
		cc.getProperties().
			put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 5);
		cc.getProperties().
			put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
		cc.getClasses().add(EnhancementStructureReader.class);
		builder = Client.create(cc);
	}

	public static <T> T get(URI uri, MediaType acceptType,
			Class<T> returnType) {
		WebResource target = builder.resource(uri);
		Builder httpRequest = target.getRequestBuilder();
		if(acceptType != null)
			httpRequest.accept(acceptType);
		return httpRequest.get(returnType);
	}
	
	public static <T> T post(URI uri, 
			Object entity, 
			MediaType acceptType,
			MediaType contentType,
			Class<T> returnType) {
		WebResource target = builder.resource(uri);
		Builder httpRequest = target.getRequestBuilder();
		if(acceptType != null)
			httpRequest.accept(acceptType);
		if(contentType != null)
			httpRequest.type(contentType);
		httpRequest.entity(entity);
		return httpRequest.post(returnType);
	}

	public static ClientResponse delete(URI uri) {
		WebResource target = builder.resource(uri);
		Builder httpRequest = target.getRequestBuilder();
		return httpRequest.delete(ClientResponse.class);
	}
	

}
