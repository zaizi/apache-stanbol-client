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
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.stanbol.client.Enhancer;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Collect all the parameters that can be sent to the enhancer in order to
 * configure both the type of enrichment and the response
 *
 * @author <a href="mailto:rharo@zaizi.com">Rafa Haro</a>
 *
 */
public class EnhancerParameters {

	public static class EnhancerParametersBuilder {
		private final EnhancerParameters parameters = new EnhancerParameters();

		public EnhancerParametersBuilder addDereferencingField(
				final String field) {
			parameters.dereferencedFields.add(field);
			return this;
		}

		public EnhancerParameters build() {
			return parameters;
		}

		public EnhancerParameters buildDefault(final String content) {
			final EnhancerParameters params = new EnhancerParameters();
			params.contentSwitch = true;
			params.stringContent = content;
			return params;
		}

		public EnhancerParametersBuilder setChain(final String chain) {
			parameters.chain = chain;
			return this;
		}

		public EnhancerParametersBuilder setContent(final InputStream content) {
			parameters.contentSwitch = false;
			parameters.content = content;
			return this;
		}

		public EnhancerParametersBuilder setContent(final String content) {
			parameters.contentSwitch = true;
			parameters.stringContent = content;
			return this;
		}

		public EnhancerParametersBuilder setLDpathProgram(
				final String ldpathProgram) {
			parameters.ldpath = Optional.of(ldpathProgram);
			return this;
		}

		public EnhancerParametersBuilder setOutputFormat(
				final OutputFormat format) {
			parameters.outputFormat = format;
			return this;
		}
	}

	/**
	 * Accepted Output Formats
	 */
	public static enum OutputFormat {
		NT(new MediaType("text", "rdf+n3")), RDFXML(new MediaType(
				"application", "rdf+xml")), TURTLE(new MediaType("text",
				"turtle"));

		public static OutputFormat get(final String type) {
			for (final OutputFormat of : OutputFormat.values()) {
				if (of.type.toString().equals(type)) {
					return of;
				}
			}
			return null;
		}

		private final MediaType type;

		private OutputFormat(final MediaType type) {
			this.type = type;
		}

		public MediaType value() {
			return type;
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

	private String chain = Enhancer.DEFAULT_CHAIN;
	private InputStream content;
	private boolean contentSwitch = false; // False -> Stream, True -> Content
	private final Collection<String> dereferencedFields = Sets.newHashSet();
	private Optional<String> ldpath = Optional.absent();
	private OutputFormat outputFormat = OutputFormat.TURTLE;
	private String stringContent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else {
			final EnhancerParameters other = (EnhancerParameters) obj;
			return (Objects.equals(getChain(), other.getChain())
					&& Objects.equals(getContent(), other.getContent())
					&& Objects.equals(getDereferencedFields(),
							other.getDereferencedFields())
					&& Objects.equals(getLdPath(), other.getLdPath()) && Objects
						.equals(getOutputFormat(), other.getOutputFormat()));
		}
	}

	public String getChain() {
		return chain;
	}

	public InputStream getContent() {
		if (!contentSwitch) {
			return content;
		} else {
			return IOUtils.toInputStream(stringContent);
		}
	}

	public Collection<String> getDereferencedFields() {
		return dereferencedFields;
	}

	public String getLdPath() {
		return ldpath.orNull();
	}

	public MediaType getOutputFormat() {
		return outputFormat.value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getChain(), getContent(), getDereferencedFields(),
				getLdPath(), getOutputFormat());
	}
}
