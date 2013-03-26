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
package org.apache.stanbol.client.contenthub.store.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.stanbol.client.contenthub.store.model.ContentHubDocumentRequest;
import org.apache.stanbol.client.contenthub.store.model.ContentItem;
import org.apache.stanbol.client.contenthub.store.model.Metadata;
import org.apache.stanbol.client.contenthub.store.services.StanbolContenthubStoreService;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.ontology.FISE;
import org.apache.stanbol.client.restclient.Parameters;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolServiceAbstract;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.uuid.UUID_V4_Gen;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Implementation of {@link StanbolContenthubStoreService}
 * 
 * @author efoncubierta
 * @author Rafa Haro
 * 
 */
public class StanbolContenthubStoreServiceImpl extends StanbolServiceAbstract implements StanbolContenthubStoreService
{
    private Logger logger = Logger.getLogger(StanbolContenthubStoreServiceImpl.class);

    /**
     * Constructor
     * 
     * @param restClient REST Client
     */
    public StanbolContenthubStoreServiceImpl(RestClient restClient)
    {
        super(restClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#add(org.apache
     * .stanbol.client.services.ContentHubRequest)
     */
    @Override
    public String add(String index, String chain, ContentHubDocumentRequest request) throws StanbolServiceException
    {

        final String contentUrl = STANBOL_CONTENTHUB_PATH + index + STANBOL_CONTENTHUB_STORE_PATH;
        FormDataMultiPart contentItem = new FormDataMultiPart();

        // Manage Metadata
        if (request.getEnhancementGraph() != null)
        {
            Resource contentItemResource = extractContentItemURI(request.getEnhancementGraph());
            
            // Add Metadata to the enhancement Graph
            if (request.getMetadata() != null && request.getMetadata().size() > 0)
            {
                addMetadata(request.getEnhancementGraph(), contentItemResource, request.getMetadata());
            }
        }
        else
        {
            if (request.getMetadata() != null)
            {
                Model metadata = ModelFactory.createDefaultModel();
                Resource contentItemResource = metadata.createResource(request.getURI());
                addMetadata(metadata, contentItemResource, request.getMetadata());
                request.setEnhancementGraph(metadata);
            }
        }

        // Include Metadata as ContentPart
        if (request.getEnhancementGraph() != null)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            request.getEnhancementGraph().write(out, "RDF/XML");
            String rdfContent = new String(out.toByteArray(), Charset.forName("UTF-8"));
            FormDataBodyPart metadataPart = null;
            try
            {
                FormDataContentDisposition cd = new FormDataContentDisposition(
                        "form-data; name=\"metadata\";filename=\"" + request.getURI() + "\"");
                metadataPart = new FormDataBodyPart(cd, rdfContent, new MediaType("application", "rdf+xml"));
            }
            catch (ParseException e)
            {
                metadataPart = new FormDataBodyPart("metadata", rdfContent, new MediaType("application", "rdf+xml"));
            }

            contentItem.bodyPart(metadataPart);
        }

        // Add Plain Text Version of the file
        if (request.getContentStream() != null)
        {
            try
            {
                FormDataBodyPart fdp = new FormDataBodyPart("content", new ByteArrayInputStream(
                        IOUtils.toByteArray(request.getContentStream())), MediaType.TEXT_PLAIN_TYPE);

                contentItem.bodyPart(fdp);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new StanbolServiceException("Error Serializing Content: " + e.getMessage());
            }
        }
        else
            throw new StanbolServiceException("No ContentFile and ContentStream detected to store in ContentHub");

        Parameters parameters = new Parameters();

        if (chain != null && !chain.equals(""))
            parameters.put("chain", chain);

        if (request.getTitle() != null && !request.getTitle().equals(""))
            parameters.put("title", request.getTitle());

        ClientResponse response = getRestClient().post(contentUrl, contentItem,
                new MediaType("application", "rdf+xml"), parameters);

        int status = response.getStatus();
        if (status != 201)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error while posting content into stanbol server");
        }

        URI location = response.getLocation();
        if (logger.isDebugEnabled())
        {
            logger.debug("Content " + request.getURI() + " has been sucessfully created at " + location);
        }

        return location.toString().replace(getRestClient().getEndpoint() + contentUrl + "/content/", "");
    }

    // Add Implementation with httpclient and httpmime

    // public URI add(String index, String chain, final ContentHubDocumentRequest request) throws
    // StanbolServiceException {
    //
    // final String contentUrl = STANBOL_CONTENTHUB_PATH + index + STANBOL_CONTENTHUB_STORE_PATH;
    //
    // Charset charset = Charset.forName("UTF-8");
    // MultipartEntity contentItem = new MultipartEntity(null, null, charset);
    //
    //
    // // Alternate Content
    // /*HttpMultipart content = new HttpMultipart("alternate", charset, "contentParts");
    //
    // contentItem.addPart(
    // "content", //the name MUST BE "content"!
    // new MultipartContentBody(content, request.getURI()));
    //
    // // Add Original Content
    // File contentFile = request.getContentFile();
    // if(contentFile != null && request.getContentStream() != null){
    // content.addBodyPart(new FormBodyPart(contentFile.getName(),
    // new InputStreamBody(request.getContentStream(),
    // new MimetypesFileTypeMap().getContentType(contentFile),
    // contentFile.getName())));
    // }*/
    //
    // // Add Plain Text Version of the file
    // if(request.getContentFile() != null && request.getContentStream() != null){
    // try
    // {
    // contentItem.addPart(new FormBodyPart(
    // "content",
    // new StringBody(
    // IOUtils.toString(request.getContentStream()), //apache commons IO utility
    // "text/plain",
    // charset)));
    // }
    // catch (Exception e){
    // e.printStackTrace();
    // throw new StanbolServiceException("Impossible to attach Content File to ContentHub Store HTTP Request. Error: " +
    // e.getMessage());
    // }
    //
    //
    //
    // // Manage Metadata
    // if(request.getEnhancementGraph() != null){
    // Resource contentItemResource = extractContentItemURI(request.getEnhancementGraph());
    // request.setURI(contentItemResource.getURI());
    //
    // // Add Metadata to the enhancement Graph
    // if(request.getMetadata() != null && request.getMetadata().size() > 0){
    // addMetadata(request.getEnhancementGraph(), contentItemResource, request.getMetadata());
    // }
    // }
    // else{
    // if(request.getMetadata() != null){
    // Model metadata = ModelFactory.createDefaultModel();
    // Resource contentItemResource = metadata.createResource(request.getURI());
    // addMetadata(metadata, contentItemResource, request.getMetadata());
    // request.setEnhancementGraph(metadata);
    // }
    // }
    //
    // // Include Metadata as ContentPart
    // if(request.getEnhancementGraph() != null)
    // {
    // ByteArrayOutputStream out = new ByteArrayOutputStream();
    // request.getEnhancementGraph().write(out, "RDF/XML");
    // String rdfContent = new String(out.toByteArray(), charset);
    //
    // try
    // {
    // contentItem.addPart("metadata",
    // new StringBody(rdfContent, "RDF/XML", charset){
    // @Override
    // public String getFilename(){
    // return request.getURI();
    // }
    // });
    // }
    // catch (UnsupportedEncodingException e)
    // {
    // e.printStackTrace();
    // throw new StanbolServiceException(e.getMessage());
    // }
    //
    //
    //
    // StringBuilder requestUrl = new StringBuilder(getRestClient().getEndpoint()+contentUrl);
    //
    // if(chain != null){
    // requestUrl.append(String.format("?%s=%s",
    // "chain", chain));
    // }
    // if (request.getTitle() != null) {
    // try
    // {
    // requestUrl.append(String.format("&%s=%s",
    // "title", URLEncoder.encode(request.getTitle(), "UTF-8")));
    // }
    // catch (UnsupportedEncodingException e)
    // {
    // requestUrl.append(String.format("&%s=%s",
    // "title", request.getTitle()));
    // }
    // }
    //
    // HttpClient client = new DefaultHttpClient();
    // HttpPost postRequest = new HttpPost(requestUrl.toString());
    // postRequest.setEntity(contentItem);
    //
    // HttpResponse response = null;
    // try
    // {
    // response = client.execute(postRequest);
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // throw new StanbolServiceException("Impossible to execute HTTP Request " + postRequest.toString() +
    // " due to the following error: " + e.getMessage());
    // }
    //
    //
    // int status = response.getStatusLine().getStatusCode();
    // if (status != 201) {
    // throw new StanbolServiceException("[HTTP " + status + "] Error while posting content into stanbol server");
    // }
    //
    // String location = response.getLastHeader("Location").getValue();
    // if (logger.isDebugEnabled()) {
    // logger.debug("Content " + request.getURI() + " has been sucessfully created at " + location);
    // }
    //
    // client.getConnectionManager().shutdown();
    //
    // try
    // {
    // return new URI(location);
    // }
    // catch (URISyntaxException e)
    // {
    // e.printStackTrace();
    // return null;
    // }
    //
    // }

    /**
     * 
     * @param enhancementGraph
     * @param contentItemResource
     * @param listMetadata
     */
    private void addMetadata(Model enhancementGraph, Resource contentItemResource, List<Metadata> listMetadata)
    {
        for (Metadata metadata : listMetadata)
        {

            String enhancementUri = metadata.getURI();
            if (enhancementUri == null || enhancementUri.equals(""))
            {
                UUID_V4_Gen generator = new UUID_V4_Gen();
                enhancementUri = "urn:user-annotation:" + generator.generate().asString();
            }
            
            Resource metadataResource = enhancementGraph.createResource(enhancementUri, FISE.USER_ANNOTATION);
            metadataResource.addProperty(RDF.type, FISE.ENHANCEMENT);
            metadataResource.addProperty(FISE.EXTRACTED_FROM, contentItemResource);

            String namespace = metadata.getNamespace();
            if (namespace == null)
                namespace = FISE.USER_METADATA_URI;

            if (!metadata.getValue().isEmpty())
            {
                for (String value : metadata.getValue())
                {
                    String metadataAnnotationUri = namespace;
                    if(!metadataAnnotationUri.endsWith("/"))
                         metadataAnnotationUri += "/";
                    metadataAnnotationUri += metadata.getName();
                    
                    Resource valueResource = enhancementGraph.createResource(metadataAnnotationUri,
                            FISE.USER_METADATA_ANNOTATION);
                    valueResource.addProperty(FISE.USER_METADATA_VALUE, value);
                    valueResource.addProperty(FISE.EXTRACTED_FROM, contentItemResource);
                    valueResource.addProperty(DCTerms.relation, metadataResource);
                    //enhancementGraph.add(metadataResource, FISE.USER_METADATA, valueResource);
                }
            }

            if (metadata.getType() != null)
                metadataResource.addProperty(DCTerms.type, metadata.getType());
            if (metadata.getCreator() != null)
                metadataResource.addProperty(DCTerms.creator, metadata.getCreator());

            //metadataResource.getModel().add(metadataResource, FISE.EXTRACTED_FROM, contentItemResource);
        }
    }

    /**
     * 
     * @param enhancementGraph
     * @return
     */
    private Resource extractContentItemURI(Model enhancementGraph)
    {
        NodeIterator iterator = enhancementGraph.listObjectsOfProperty(FISE.EXTRACTED_FROM);
        while (iterator.hasNext())
        {
            RDFNode node = iterator.next();
            if (node.isResource())
                return node.asResource();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#delete(java .lang.String, java.lang.String)
     */
    @Override
    public void delete(String index, String id) throws StanbolServiceException
    {
        final String contentUrl = STANBOL_CONTENTHUB_PATH + index + STANBOL_CONTENTHUB_STORE_PATH + "/" + id;

        // Delete content from Stanbol server
        ClientResponse response = getRestClient().delete(contentUrl);

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error deleting content from stanbol server");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#get(java. lang.String, java.lang.String)
     */
    @Override
    public ContentItem get(String index, String uri, boolean downloadContent) throws StanbolServiceException
    {
        final String contentUrl = STANBOL_CONTENTHUB_PATH + index + STANBOL_CONTENTHUB_STORE_PATH;

        // Retrieve metadata from Stanbol server
        ClientResponse response = getRestClient().get(contentUrl + STANBOL_CONTENTHUB_METADATA_PATH + uri,
                new MediaType("application", "rdf+xml"));

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 404)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error retrieving content from stanbol server");
        }

        // Not Found
        if (status == 404)
        {
            logger.info("ContentItem with URI " + uri + " not found in ContenHub index " + index);
            return null;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Content " + uri + " has been sucessfully loaded from " + contentUrl);
        }

        if (downloadContent)
        {
            // Retrieve ContentItem Raw Content from Stanbol Server
            ClientResponse rawResponse = getRestClient().get(contentUrl + STANBOL_CONTENTHUB_RAW_PATH + uri,
                    MediaType.TEXT_PLAIN_TYPE);

            status = rawResponse.getStatus();
            if (status != 200)
            {
                logger.info("[HTTP " + status + "] Error retrieving raw content from stanbol server");
            }
            else
            {
                return parse(response.getEntityInputStream(), rawResponse.getEntityInputStream());
            }
        }

        return parse(response.getEntityInputStream(), null);
    }

    /**
     * Extract a content item from an InputStream
     * 
     * @param id Content id
     * @param is Content input stream
     * @return Content item
     */
    private ContentItem parse(InputStream is, InputStream raw)
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null);

        return new ContentItem(model, raw);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#createIndex(java.lang.String,
     * org.apache.stanbol.client.model.LDPathProgram)
     */
    @Override
    public URI createIndex(String name, LDPathProgram program) throws StanbolServiceException
    {
        final String ldPathUrl = STANBOL_CONTENTHUB_PATH + STANBOL_CONTENTHUB_LDPATH_PATH + "/program";

        Parameters par = new Parameters();
        par.put("name", name);
        par.put("program", program.toString());

        ClientResponse response = getRestClient().post(ldPathUrl, MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                MediaType.APPLICATION_XML_TYPE, par);

        int status = response.getStatus();

        if (status == 500)
        {
            throw new StanbolServiceException("There is already an index with name " + name
                    + " or an error has been produced trying to parse the LDProgram " + program.toString()
                    + ". Please check Stanbol Server logs");
        }

        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error while posting content into stanbol ContentHub");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Index " + name + " with LDProgram " + program.toString()
                    + " has been sucessfully created at " + response.getLocation());
        }

        return response.getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#deleteIndex(java.lang.String)
     */
    public void deleteIndex(String name) throws StanbolServiceException
    {
        final String ldPathUrl = STANBOL_CONTENTHUB_PATH + STANBOL_CONTENTHUB_LDPATH_PATH + "/program/" + name;

        ClientResponse response = getRestClient().delete(ldPathUrl);

        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status + "] Error while deleting ContentHub index " + name);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Index " + name + " has been sucessfully deleted");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.stanbol.client.services.StanbolContenthubService#getIndexes()
     */
    @Override
    public Map<String, LDPathProgram> getIndexes() throws StanbolServiceException
    {
        final String indexesUrl = STANBOL_CONTENTHUB_PATH + STANBOL_CONTENTHUB_LDPATH_PATH;

        // Retrieve metadata from Stanbol server
        ClientResponse response = getRestClient().get(indexesUrl, MediaType.APPLICATION_JSON_TYPE);

        // Check HTTP status code
        int status = response.getStatus();
        if (status != 200 && status != 201 && status != 202)
        {
            throw new StanbolServiceException("[HTTP " + status
                    + "] Error retrieving indexes list from Stanbol ContentHub");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("List of ContentHub's indexes sucessfully loaded from " + indexesUrl);
        }

        JSONObject responseObject = response.getEntity(JSONObject.class);
        Map<String, LDPathProgram> result = new HashMap<String, LDPathProgram>();
        while (responseObject.keys().hasNext())
        {
            String nextIndex = (String) responseObject.keys().next();
            LDPathProgram nextProgram = null;
            try
            {
                nextProgram = new LDPathProgram(responseObject.getString(nextIndex));
            }
            catch (StanbolClientException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally
            { // If LDPathProgram can't be parsed, at least store the name of the index
                result.put(nextIndex, nextProgram);
            }
        }

        return result;
    }
}