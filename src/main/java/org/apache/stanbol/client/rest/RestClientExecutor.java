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
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.stanbol.client.enhancer.model.EnhancementStructure.EnhancementStructureReader;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * RestEasy based implementation REST operations
 * 
 * @author Rafa Haro <rharo@zaizi.com>
 *
 */
public class RestClientExecutor {
	
	private static final int TIMEOUT = 60;
	
	private static ResteasyClientBuilder builder;
	
	static{
		builder = new ResteasyClientBuilder();
		builder.connectionPoolSize(5);
		builder.establishConnectionTimeout(TIMEOUT, TimeUnit.SECONDS);
		builder.register(EnhancementStructureReader.class);
	}

	public static Response get(URI uri, MediaType acceptType) {
		WebTarget target = builder.build().target(uri);
		Builder httpRequest = target.request();
		if(acceptType != null)
			httpRequest.accept(acceptType);
		return httpRequest.get();
	}
	
	public static Response post(URI uri, Entity<?> entity, MediaType acceptType){
		WebTarget target = builder.build().target(uri);
		Builder httpRequest = target.request();
		if(acceptType != null)
			httpRequest.accept(acceptType);
		return httpRequest.post(entity);
	}

	public static Response delete(URI uri) {
		WebTarget target = builder.build().target(uri);
		Builder httpRequest = target.request();
		return httpRequest.delete();
	}
	

}
