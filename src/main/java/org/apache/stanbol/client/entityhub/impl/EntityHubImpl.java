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
package org.apache.stanbol.client.entityhub.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;

import org.apache.stanbol.client.EntityHub;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.rest.RestClientExecutor;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;

/**
 * EntityHub Service Client Implementation
 * 
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 *
 */
public class EntityHubImpl implements EntityHub {

	private static String createUnknownResponseErrorMessageString(
			StatusType statusInfo) {
		return "Received unknown response from server: [HTTP "
				+ statusInfo.getStatusCode() + "] "
				+ statusInfo.getReasonPhrase();
	}

	private Logger logger = LoggerFactory.getLogger(EntityHubImpl.class);

	private UriBuilder builder;

	/**
	 * Constructor
	 * 
	 */
	public EntityHubImpl(UriBuilder builder) {
		this.builder = builder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#getReferencedSites()
	 */
	@Override
	public Collection<String> getReferencedSites()
			throws StanbolServiceException, StanbolClientException {
		List<String> result;

		UriBuilder clientBuilder = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path(STANBOL_ENTITYHUB_SITEMANAGER_PATH).path("referenced");

		URI uri = clientBuilder.build();
		Response response = RestClientExecutor.get(uri, new MediaType(
				"application", "rdf+xml"));

		// Check HTTP status code
		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			throw new StanbolClientException(
					String.format(
							"An error occurred retrieving content from Stanbol server: [HTTP %d] %s",
							statusInfo.getStatusCode(),
							statusInfo.getReasonPhrase()));
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					" Error retrieving content from Stanbol server: [HTTP "
							+ statusCode + "]  " + statusInfo.getReasonPhrase());
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Sites sucessfully retrieved from "
						+ response.getLocation());
			}
			JSONArray array = response.readEntity(JSONArray.class);
			result = new ArrayList<String>(array.length());
			for (int i = 0; i < array.length(); i++) {
				try {
					result.add(array.getString(i));
				} catch (JSONException e) {
					String message = "Malformed JSON response for EntityHub referenced service";
					logger.error(message);
					throw new StanbolServiceException(message);
				}
			}
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#get(java.lang.String)
	 */
	@Override
	public Entity get(String id) throws StanbolServiceException,
			StanbolClientException {
		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH).path("entity")
				.queryParam("id", id).build();
		return getAux(uri, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#get(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Entity get(String site, String id) throws StanbolServiceException,
			StanbolClientException {
		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path(STANBOL_ENTITYHUB_SITE_PATH).path(site).path("entity")
				.queryParam("id", id).build();
		return getAux(uri, id);
	}

	private Entity getAux(URI uri, String id) throws StanbolServiceException,
			StanbolClientException {
		Entity result;

		Response response = RestClientExecutor.get(uri, new MediaType(
				"application", "rdf+xml"));

		// Check HTTP status code
		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			if (Status.NOT_FOUND.equals(Status.fromStatusCode(statusCode))) {
				result = null;
			} else {
				throw new StanbolClientException(
						String.format(
								"An error occurred retrieving content from Stanbol server: [HTTP %d] %s",
								statusCode, statusInfo.getReasonPhrase()));
			}
		}
		case SERVER_ERROR: {
			throw new StanbolServiceException(
					String.format(
							"An error occurred retrieving content from Stanbol server: [HTTP %d] %s",
							statusInfo.getStatusCode(),
							statusInfo.getReasonPhrase()));
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Content " + id
						+ " has been sucessfully loaded from "
						+ response.getLocation());
			}
			result = parse(id, response.readEntity(InputStream.class), false);
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#create(java.io.InputStream,
	 * java.lang.String, java.lang.Boolean)
	 */
	@Override
	public String create(InputStream is, String id, Boolean update)
			throws StanbolServiceException, StanbolClientException {
		String result;

		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH).path("entity")
				.queryParam("id", id).queryParam("update", update).build();
		javax.ws.rs.client.Entity<?> entity = javax.ws.rs.client.Entity.entity(
				is, new MediaType("application", "rdf+xml"));

		Response response = RestClientExecutor.post(uri, entity,
				MediaType.TEXT_XML_TYPE);

		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			final String errorMessage = (Status.BAD_REQUEST.equals(Status.fromStatusCode(statusCode))) ? id
					+ " already exists within EntityHub. You might want to pass updated param with a true value"
					: String.format(
							"An unknown client error occurred while trying to add entity for ID \"%s\": [HTTP %d] %s",
							id, statusCode, statusInfo.getReasonPhrase());
			throw new StanbolClientException(errorMessage);
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					"Error while posting content into Stanbol EntityHub: [HTTP "
							+ statusCode + "] " + statusInfo.getReasonPhrase());
		}
		case SUCCESSFUL: {
			final URI location = response.getLocation();
			if (logger.isDebugEnabled()) {
				logger.debug("Entity " + id
						+ " has been sucessfully created at " + location);
			}
			result = location.toString();
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.stanbol.client.EntityHub#create(org.apache.stanbol.client.
	 * entityhub.model.Entity, java.lang.Boolean)
	 */
	@Override
	public String create(Entity entity, Boolean update)
			throws StanbolServiceException, StanbolClientException {
		return create(entity.getStream(), entity.getUri(), update);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#update(java.io.InputStream,
	 * java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Entity update(InputStream is, String id, Boolean create)
			throws StanbolServiceException, StanbolClientException {
		Entity result;

		UriBuilder createBuilder = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path("entity");

		if (id != null && !id.equals(""))
			createBuilder = createBuilder.queryParam("id", id);
		if (!create)
			createBuilder = createBuilder.queryParam("create",
					create.toString());

		URI uri = createBuilder.build();

		javax.ws.rs.client.Entity<?> entity = javax.ws.rs.client.Entity.entity(
				is, new MediaType("application", "rdf+xml"));

		Response response = RestClientExecutor.post(uri, entity, new MediaType(
				"application", "rdf+xml"));

		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			final String errorMessage = (Status.BAD_REQUEST.equals(Status.fromStatusCode(statusCode))) ? id
					+ " already exists within EntityHub. You might want to pass updated param with a true value"
					: String.format(
							"An unknown client error occurred while trying to update entity for ID \"%s\": [HTTP %d] %s",
							id, statusCode, statusInfo.getReasonPhrase());
			throw new StanbolClientException(errorMessage);
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					"Error while posting content into Stanbol EntityHub: [HTTP "
							+ statusCode + "] " + statusInfo.getReasonPhrase());
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Entity " + id
						+ " has been sucessfully updated at "
						+ response.getLocation());
			}
			result = parse(id, response.readEntity(InputStream.class), false);
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.stanbol.client.EntityHub#update(org.apache.stanbol.client.
	 * entityhub.model.Entity, java.lang.Boolean)
	 */
	@Override
	public Entity update(Entity entity, Boolean create)
			throws StanbolServiceException, StanbolClientException {
		return update(entity.getStream(), entity.getUri(), create);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#delete(java.lang.String)
	 */
	@Override
	public Boolean delete(String id) throws StanbolServiceException,
			StanbolClientException {
		return deleteAux(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#deleteAll()
	 */
	@Override
	public Boolean deleteAll() throws StanbolServiceException,
			StanbolClientException {
		return deleteAux("*");
	}

	private Boolean deleteAux(String id) throws StanbolServiceException,
			StanbolClientException {
		Boolean result;

		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH).path("entity")
				.queryParam("id", id).build();

		Response response = RestClientExecutor.delete(uri);

		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			if (Status.NOT_FOUND.equals(Status.fromStatusCode(statusCode))) {
				result = false;
			} else {
				throw new StanbolClientException(
						String.format(
								"An unknown client error occurred while deleting content for entity ID \"%s\": [HTTP %d] %s",
								id, statusCode, statusInfo.getReasonPhrase()));
			}
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					"Error while deleting content from Stanbol server: [HTTP "
							+ statusCode + "] " + statusInfo.getReasonPhrase());
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Entity " + id
						+ " has been sucessfully deleted at "
						+ response.getLocation());
			}

			result = true;
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.stanbol.client.EntityHub#lookup(java.lang.String,
	 * java.lang.Boolean)
	 */
	@Override
	public Entity lookup(String id, Boolean create)
			throws StanbolServiceException, StanbolClientException {
		Entity result;

		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH).path("lookup")
				.queryParam("id", id).queryParam("create", create.toString())
				.build();

		Response response = RestClientExecutor.get(uri, new MediaType(
				"application", "rdf+xml"));

		// Check HTTP status code
		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			final Status status = Status.fromStatusCode(statusCode);
			if (status == null) {
				throw new StanbolClientException(
						String.format(
								"An unknown client error occurred while retrieving content for entity ID \"%s\": [HTTP %d] %s",
								id, statusCode, statusInfo.getReasonPhrase()));
			} else {
				switch (status) {
				case FORBIDDEN: {
					throw new StanbolClientException(
							"Creation of new Symbols is not allowed in the current Stanbol Configuration");
				}
				case NOT_FOUND: {
					result = null;
					break;
				}
				default: {
					throw new StanbolClientException(
							String.format(
									"An unknown client error occurred while retrieving content for entity ID \"%s\": [HTTP %d] %s",
									id, statusCode,
									statusInfo.getReasonPhrase()));
				}
				}
			}
			break;
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					"Error retrieving content from Stanbol server: [HTTP "
							+ statusCode + "] " + statusInfo.getReasonPhrase());
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Entity " + id
						+ " has been sucessfully looked up from "
						+ response.getLocation());
			}

			result = parse(id, response.readEntity(InputStream.class), true);
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}

		return result;
	}

	/**
	 * @throws StanbolClientException 
	 * @see EntityHub#search(String, String, String, LDPathProgram, int, int)
	 */
	@Override
	public Collection<Entity> search(String name, String field,
			String language, LDPathProgram ldpath, int limit, int offset)
			throws StanbolServiceException, StanbolClientException {

		UriBuilder findBuilder = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path("find").queryParam("name", name);

		if (field != null && !field.equals(""))
			findBuilder = findBuilder.queryParam("field", field);
		if (language != null && !language.equals(""))
			findBuilder = findBuilder.queryParam("language", language);

		URI uri = findBuilder.queryParam("ldpath", ldpath.toString())
				.queryParam("limit", "" + limit)
				.queryParam("offset", "" + offset).build();

		return searchAux(uri, name);
	}

	/**
	 * @throws StanbolClientException 
	 * @see EntityHub#search(String, String, String, String, LDPathProgram, int,
	 *      int)
	 */
	@Override
	public Collection<Entity> search(String site, String name, String field,
			String language, LDPathProgram ldpath, int limit, int offset)
			throws StanbolServiceException, StanbolClientException {
		UriBuilder findBuilder = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path(STANBOL_ENTITYHUB_SITE_PATH).path(site).path("find")
				.queryParam("name", name);

		if (field != null && !field.equals(""))
			findBuilder = findBuilder.queryParam("field", field);
		if (language != null && !language.equals(""))
			findBuilder = findBuilder.queryParam("language", language);

		URI uri = findBuilder.queryParam("ldpath", ldpath.toString())
				.queryParam("limit", "" + limit)
				.queryParam("offset", "" + offset).build();

		return searchAux(uri, name);
	}

	private List<Entity> searchAux(URI uri, String name)
			throws StanbolServiceException, StanbolClientException {
		List<Entity> result;
		
		Response response = RestClientExecutor.get(uri, new MediaType(
				"application", "rdf+xml"));

		// Check HTTP status code
		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolClientException(
					String.format(
							"An unknown client error occurred while retrieving Stanbol content for URI \"%s\": [HTTP %d] %s",
							uri, statusCode, statusInfo.getReasonPhrase()));
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					String.format(
							"An unknown server error occurred while retrieving Stanbol content for URI \"%s\": [HTTP %d] %s",
							uri, statusCode, statusInfo.getReasonPhrase()));
		}
		case SUCCESSFUL: {
			if (logger.isDebugEnabled()) {
				logger.debug("Entities by " + name + " has been found sucessfully "
						+ response.getLocation());
			}

			Model model = ModelFactory.createDefaultModel();
			model.read(response.readEntity(InputStream.class), null);

			result = new ArrayList<>();
			ResIterator iterator = model.listSubjects();

			while (iterator.hasNext()) {
				RDFNode next = iterator.next();
				if (next.isResource())
					if (!next
							.asResource()
							.getURI()
							.equals("http://stanbol.apache.org/ontology/entityhub/query#QueryResultSet"))
						result.add(new Entity(next.asResource().getModel(), next
								.asResource().getURI()));
			}
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}
		
		return result;
	}

	/**
	 * @throws StanbolClientException 
	 * @see EntityHub#ldpath(String, LDPathProgram)
	 */
	@Override
	public Model ldpath(String context, LDPathProgram ldPathProgram)
			throws StanbolServiceException, StanbolClientException {
		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH).path("ldpath")
				.queryParam("context", context.toString())
				.queryParam("ldpath", ldPathProgram.toString()).build();
		return ldpathAux(uri);
	}

	/**
	 * @throws StanbolClientException 
	 * @see EntityHub#ldpath(String, String, LDPathProgram)
	 */
	@Override
	public Model ldpath(String site, String context, LDPathProgram ldPathProgram)
			throws StanbolServiceException, StanbolClientException {
		URI uri = builder.clone().path(STANBOL_ENTITYHUB_PATH)
				.path(STANBOL_ENTITYHUB_SITE_PATH).path(site).path("ldpath")
				.queryParam("context", context.toString())
				.queryParam("ldpath", ldPathProgram.toString()).build();
		return ldpathAux(uri);
	}

	private Model ldpathAux(URI uri) throws StanbolServiceException, StanbolClientException {
		Model result;
		
		Response response = RestClientExecutor.get(uri, new MediaType(
				"application", "rdf+xml"));

		// Check HTTP status code
		final StatusType statusInfo = response.getStatusInfo();
		switch (statusInfo.getFamily()) {
		case CLIENT_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolClientException(
					String.format(
							"An unknown client error occurred while retrieving Stanbol content for URI \"%s\": [HTTP %d] %s",
							uri, statusCode, statusInfo.getReasonPhrase()));
		}
		case SERVER_ERROR: {
			final int statusCode = statusInfo.getStatusCode();
			throw new StanbolServiceException(
					String.format(
							"An unknown server error occurred while retrieving Stanbol content for URI \"%s\": [HTTP %d] %s",
							uri, statusCode, statusInfo.getReasonPhrase()));
		}
		case SUCCESSFUL: {
			result = ModelFactory.createDefaultModel();
			result.read(response.readEntity(InputStream.class), null);
			break;
		}
		default: {
			throw new StanbolServiceException(
					createUnknownResponseErrorMessageString(statusInfo));
		}
		}
		
		return result;
	}

	/**
	 * Extract a content item from an InputStream
	 * 
	 * @param id
	 *            Content id
	 * @param is
	 *            Content input stream
	 * @return Content item
	 */
	private Entity parse(String id, InputStream is, Boolean extractId) {
		Model model = ModelFactory.createDefaultModel();
		model.read(is, null);

		if (extractId)
			return new Entity(model, model.listSubjects().next().getURI()
					.replace(".meta", ""));
		else
			return new Entity(model, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((builder == null) ? 0 : builder.hashCode());
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EntityHubImpl other = (EntityHubImpl) obj;
		if (builder == null) {
			if (other.builder != null) {
				return false;
			}
		} else if (!builder.equals(other.builder)) {
			return false;
		}
		if (logger == null) {
			if (other.logger != null) {
				return false;
			}
		} else if (!logger.equals(other.logger)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EntityHubImpl [logger=");
		sb.append(logger);
		sb.append(", builder=");
		sb.append(builder);
		sb.append("]");
		return sb.toString();
	}

}
