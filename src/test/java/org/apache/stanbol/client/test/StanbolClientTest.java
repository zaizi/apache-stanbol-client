package org.apache.stanbol.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.stanbol.client.StanbolClient;
import org.apache.stanbol.client.contenthub.search.model.FacetResult;
import org.apache.stanbol.client.contenthub.search.model.StanbolSearchResult;
import org.apache.stanbol.client.contenthub.search.model.StanbolSolrVocabulary.SolrFieldName;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubFeaturedSearchService;
import org.apache.stanbol.client.contenthub.search.services.StanbolContenthubSolrSearchService;
import org.apache.stanbol.client.contenthub.store.model.ContentHubDocumentRequest;
import org.apache.stanbol.client.contenthub.store.model.ContentItem;
import org.apache.stanbol.client.contenthub.store.model.Metadata;
import org.apache.stanbol.client.contenthub.store.services.StanbolContenthubStoreService;
import org.apache.stanbol.client.enhancer.model.EnhancementResult;
import org.apache.stanbol.client.enhancer.model.EntityAnnotation;
import org.apache.stanbol.client.enhancer.model.TextAnnotation;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.impl.StanbolClientImpl;
import org.apache.stanbol.client.ontology.FISE;
import org.apache.stanbol.client.sparql.services.StanbolSparqlService;
import org.junit.Test;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

public class StanbolClientTest
{

    private static final String STANBOL_ENDPOINT = "http://localhost:8080/";
    
    private static final String TEST_EN_FILE = "test_en.txt";
    private static final String TEST_EN2_FILE = "test_en2.txt";
    private static final String TEST_ES_FILE = "test_es.txt";
    private static final String TEST_RDF_FILE = "Doctor_Who.txt";
    
    private static final String TEST_INDEX_NAME = "TestIndex";
    private static final String TEST_INDEX_LDPROGRAM = "@prefix find:<http://stanbol.apache.org/ontology/entityhub/find/>; find:labels = rdfs:label[@en] :: xsd:string; find:comment = rdfs:comment[@en] :: xsd:string; find:categories = dc:subject :: xsd:anyURI; find:mainType = rdf:type :: xsd:anyURI;";
    
    private static final String TEST_URI = "workspace://SpacesStore/d8185c88-44bb-49d4-85a7-2ae2c1028a2c";
    //private static final String TEST_URI = "urn:content-item-sha1-04617f62be8dbd432f286ff1d69a4118be9c5062";
    
    private static final String SPARQL_QUERY = "PREFIX fise: <http://fise.iks-project.eu/ontology/>" +
    		"PREFIX dc:   <http://purl.org/dc/terms/>" +
    		"SELECT distinct ?enhancement ?content ?engine ?extraction_time" +
    		"WHERE {" +
    		"  ?enhancement a fise:TextAnnotation ." +
    		"  ?enhancement fise:extracted-from ?content ." +
    		"  ?enhancement dc:creator ?engine ." +
    		"  ?enhancement dc:created ?extraction_time ." +
    		"}" +
    		"ORDER BY DESC(?extraction_time) LIMIT 5";

    @Test
    public void testClient() throws Exception
    {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        
        EnhancementResult eRes = client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
        assertTrue(eRes.getEntityAnnotations().size() == 5);
        eRes.filterByConfidence(0.2);
        assertTrue(eRes.getEntityAnnotations().size() == 3);

        EnhancementResult enhancements = client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
        assertNotNull(enhancements);
        assertFalse(enhancements.getEnhancements().size() == 0);
        assertTrue(enhancements.getEntityAnnotations().size() == 5);
        
        assertEquals(enhancements.getEntityAnnotations().iterator().next().getSite(), "dbpedia");
        
        List<String> labels = new ArrayList<String>();
        for(EntityAnnotation ea:enhancements.getEntityAnnotations())
            labels.add(ea.getEntityLabel());
        
        assertTrue(labels.contains("Paris"));
        assertTrue(labels.contains("France"));
        
        enhancements.disambiguate();
        for(TextAnnotation ta:enhancements.getTextAnnotations()){
            Collection<EntityAnnotation> eas = enhancements.getEntityAnnotations(ta);
            if(eas.size() > 0)
                assertTrue(eas.size() == 1);
        }
        
        enhancements = client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
        enhancements.filterByConfidence(0.6);
        for(TextAnnotation ta:enhancements.getTextAnnotations()){
            Collection<EntityAnnotation> eas = enhancements.getEntityAnnotations(ta);
            if(eas.size() > 0)
                assertTrue(eas.size() == 1);
        }
        
        enhancements = client.enhancer().enhance(TEST_URI,
                this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        assertNotNull(enhancements);
        assertFalse(enhancements.getEnhancements().size() == 0);
    }

    @Test
    public void testContentHub() throws Exception
    {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);

        // contenthub request
        ContentHubDocumentRequest request = new ContentHubDocumentRequest();

        request.setURI(TEST_URI);
        request.setTitle("Test 1");

        File testFile = new File(TEST_EN_FILE);
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE);
        request.setContentFile(testFile);
        request.setContentStream(stream);

        // add content
        String docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);

        // load content
        ContentItem ci = client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, true);
        
        // checks
        assertNotNull(ci);
        assertFalse(ci.getEnhancements().size() == 0);
        assertNotNull(ci.getEnhancementResult());
        assertFalse(ci.getRawContent() == null);
        assertTrue(ci.getMetadata().isEmpty());
        assertNotNull(ci.getEnhancementGraph());

        // deleteContent
        client.contenthub().delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);
        assertTrue(client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, true) == null);

        // Content with Custom Metadata
        List<Metadata> metadata = new ArrayList<Metadata>();
        metadata.add(new TestMetadata("Name1", Arrays.asList("value1", "value11")));
        metadata.add(new TestMetadata("Name2", Arrays.asList("value2")));

        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        request.setMetadata(metadata);
        docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        ci = client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, true);
        assertTrue(docUri.equals(request.getURI()));
        assertTrue(ci.getEnhancementCount() == 0);
        assertTrue(ci.getMetadata().size() == 2);
        assertTrue(ci.getMetadata("Name1").getValue().contains("value1"));
        assertTrue(ci.getEnhancementGraph().listResourcesWithProperty(RDF.type, FISE.USER_ANNOTATION).hasNext());
        client.contenthub().delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);

        // Content with previous enhancements
        EnhancementResult enhancements = client.enhancer().enhance(TEST_URI,
                this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        request.setEnhancementGraph(enhancements.getEnhancementGraph());
        request.setMetadata(null);
        docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        ci = client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, false);
        assertTrue(ci.getEnhancementCount() == enhancements.getEnhancements().size());
        assertTrue(ci.getURI().equals(request.getURI()));
               
        // Content with previous filtered enhancements
        enhancements.filterByConfidence(0.9d);
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        request.setEnhancementGraph(enhancements.getEnhancementGraph());
        docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        ci = client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, false);
        assertTrue(ci.getEnhancementCount() == enhancements.getEnhancements().size());

        // Content with previous enhancements and user metadata
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        request.setMetadata(metadata);
        docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        ci = client.contenthub().get(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri, false);
        assertTrue(ci.getEnhancementCount() == enhancements.getEnhancements().size());
        assertTrue(ci.getMetadata().size() == 2);
        assertTrue(ci.getMetadata("Name1").getValue().contains("value1"));
        assertTrue(ci.getURI().equals(request.getURI()));
        
        // Content with LDPathProgram
        String oldDocUri = docUri;
        client.contenthub().createIndex(TEST_INDEX_NAME, new LDPathProgram(TEST_INDEX_LDPROGRAM));
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        docUri = client.contenthub().add(TEST_INDEX_NAME, "default", request);
        ci = client.contenthub().get(TEST_INDEX_NAME, docUri, false);
        assertTrue(ci.getMetadata("Name1").getValue().contains("value1"));

        client.contenthub().delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, oldDocUri);
        client.contenthub().delete(TEST_INDEX_NAME, docUri);
        client.contenthub().deleteIndex(TEST_INDEX_NAME);
    }
    
    @Test
    public void testContentHubFeaturedSearch() throws Exception
    {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        final StanbolContenthubFeaturedSearchService featuredService = client.featuredSearch();
        final StanbolContenthubStoreService storeService = client.contenthub();
     
        // Setting Up ContentItem Request
        ContentHubDocumentRequest request = new ContentHubDocumentRequest();
        request.setURI(TEST_URI);
        request.setTitle("Test 1");
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE);
        request.setContentStream(stream);

        // add content
        String docUri = storeService.add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        
        //Test simple FeaturedSearch
        StanbolSearchResult searchResult = featuredService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "Murdoch", null, null, null, 0, 0);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getFacetResults().size() == 4);
        FacetResult facet = searchResult.getFacetResult(SolrFieldName.ORGANIZATIONS.toString());
        assertTrue(facet.getFacetField().getValueCount() == 3);
        assertTrue(facet.getFacetField().getValues().get(0).getName().equals("BBC"));
        
        
        // Test simple Solr FeaturedSearch
        SolrQuery query = new SolrQuery("Murdoch");
        searchResult = featuredService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, null, query.toString(), null, null, 0, 0);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getFacetResults().size() == 0);
        
        
        //Featured Search with Facet Constrains
        request.setURI("StanbolContentHubTest:Test2");
        request.setTitle("Test 2");
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN2_FILE));
        String docUri2 = storeService.add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
                
        Map<String, List<String>> constrains = new HashMap<String, List<String>>();
        String[] values = {"BBC"};
        constrains.put(SolrFieldName.ORGANIZATIONS.toString(), Arrays.asList(values));
        searchResult = featuredService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", null, constrains, null, 0, 0);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getFacetResult(SolrFieldName.ORGANIZATIONS.toString()).getFacetField().getValues().get(0).getName().equals("BBC"));
        
        // Test offset and limit
        searchResult = featuredService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", null, null, null, 1, 0);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getItemResults().get(0).getLocalId().equals(docUri));
        
        // Test Facet fields selection
        List<String> filters = new ArrayList<String>();
        filters.add(SolrFieldName.ORGANIZATIONS.toString());
        searchResult = featuredService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", filters);
        assertTrue(searchResult.getFacetResults().size() == 1);
        assertTrue(searchResult.getFacetResults().get(0).getFacetField().getName().equals(SolrFieldName.ORGANIZATIONS.toString()));
        
        // Test Facet Filtering via API
        searchResult = featuredService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", constrains);
        assertTrue(searchResult.getItemResults().size() == 1);
        facet = searchResult.getFacetResult(SolrFieldName.ORGANIZATIONS.toString());
        assertTrue(facet.getFacetField().getValues().get(0).getName().equals("BBC"));
     
        //Test Complex SolrQuery
        query = new SolrQuery("people");
        searchResult = featuredService.solrSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, query.toString());
        assertTrue(searchResult.getItemResults().size() == 2);
        query.addFilterQuery(SolrFieldName.PEOPLE.toString() + ":Michael Jordan");
        searchResult = featuredService.solrSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, query);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getItemResults().get(0).getLocalId().equals(docUri2));
        
     // Test Solr Faceted Search
        filters.add(SolrFieldName.PLACES.toString());
        searchResult = featuredService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, query, filters);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getItemResults().get(0).getLocalId().equals(docUri2));
        assertTrue(searchResult.getFacetResults().size() == 2);
        assertNotNull(searchResult.getFacetResult(SolrFieldName.PLACES.toString()));
        assertNotNull(searchResult.getFacetResult(SolrFieldName.ORGANIZATIONS.toString()));
        
        query = new SolrQuery("people&fq=people_t:Michael Jordan");
        filters.remove(0);
        searchResult = featuredService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, query, filters);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getItemResults().get(0).getLocalId().equals(docUri2));
        assertTrue(searchResult.getFacetResults().size() == 1);
        assertNotNull(searchResult.getFacetResult(SolrFieldName.PLACES.toString()));
        
        
        storeService.delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);
        storeService.delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri2);
    }
    
    @Test
    public void testContentHubSolrSearch() throws Exception
    {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        final StanbolContenthubSolrSearchService solrService = client.solrSearch();
        final StanbolContenthubStoreService storeService = client.contenthub();
     
        // Setting Up ContentItem Request
        ContentHubDocumentRequest request = new ContentHubDocumentRequest();
        request.setURI("StanbolContentHubTest:Test1");
        request.setTitle("Test 1");
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE);
        request.setContentStream(stream);

        // add content
       String docUri = storeService.add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
        
        request.setURI("StanbolContentHubTest:Test2");
        request.setTitle("Test 2");
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN2_FILE));
        String doc2 = storeService.add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
                
        //Test SolrQuery
        SolrQuery sQuery = new SolrQuery();
        sQuery.setQuery("people");
        StanbolSearchResult searchResult = solrService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, sQuery);
        assertTrue(searchResult.getItemResults().size() == 2);
        sQuery.addFilterQuery(SolrFieldName.ORGANIZATIONS.toString() + ":BBC");
        searchResult = solrService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, sQuery);
        assertTrue(searchResult.getItemResults().size() == 1);
        searchResult = solrService.search(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people AND people_t:\"Michael Jordan\"");
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getItemResults().get(0).getLocalId().equals(doc2));
        
        // Test Facet Constrains
        Map<String, List<String>> constrains = new HashMap<String, List<String>>();
        String[] values = {"BBC"};
        constrains.put(SolrFieldName.ORGANIZATIONS.toString(), Arrays.asList(values));
        searchResult = solrService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", constrains);
        assertTrue(searchResult.getItemResults().size() == 1);
        assertTrue(searchResult.getFacetResult(SolrFieldName.ORGANIZATIONS.toString()).getFacetField().getValues().get(0).getName().equals("BBC"));
        
     // Test Facet fields selection
        List<String> filters = new ArrayList<String>();
        filters.add(SolrFieldName.ORGANIZATIONS.toString());
        searchResult = solrService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", filters);
        assertTrue(searchResult.getFacetResults().size() == 1);
        assertTrue(searchResult.getFacetResults().get(0).getFacetField().getName().equals(SolrFieldName.ORGANIZATIONS.toString()));
        
        // Test Constraints and fields
        searchResult = solrService.facetedSearch(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "people", constrains, filters);
        assertTrue(searchResult.getFacetResults().size() == 1);
        assertTrue(searchResult.getFacetResults().get(0).getFacetField().getName().equals(SolrFieldName.ORGANIZATIONS.toString()));
        assertTrue(searchResult.getItemResults().size() == 1);
        
        storeService.delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);
        storeService.delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, doc2);
    }

    @Test
    public void testEntityHub() throws Exception
    {

        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        final String resourceId = "http://dbpedia.org/resource/Doctor_Who";
        final String parisId = "http://dbpedia.org/resource/Paris";
        final String ldPathProgram = "@prefix find:<http://stanbol.apache.org/ontology/entityhub/find/>; find:labels = rdfs:label[@en] :: xsd:string; find:comment = rdfs:comment[@en] :: xsd:string; find:categories = dc:subject :: xsd:anyURI; find:mainType = rdf:type :: xsd:anyURI;";

        // Create the entity
        String id = client.entityhub().create(this.getClass().getClassLoader().getResourceAsStream(TEST_RDF_FILE),
                resourceId, true);
        assertNotNull(id);
        assertTrue(id.toString().indexOf(resourceId) != -1);

        // Get the entity
        Entity entity = client.entityhub().get(resourceId);
        assertNotNull(entity);
        assertTrue(entity.getLabels("en").get(0).equals("Doctor Who"));
        assertTrue(entity.getUri().equals(resourceId));

        // Test Entity Model
        assertTrue(entity.getReferencedSite().equals("entityhub"));
        assertTrue(entity.getCategories().get(0).equals("BBC television programmes"));
        assertTrue(entity.getTypes().get(0).equals("Creative Work"));
        assertNotNull(entity.getComment("en").get(0));
        List<String> labels = entity.getLabels("en");
        assertTrue(labels.size() == 1);
        assertTrue(entity.getLabels("en").get(0).equals("Doctor Who"));
        assertTrue(entity.getPropertyValue("http://dbpedia.org/property/", "numEpisodes").get(0).equals("777"));

        // Remove the entity
        boolean removed = client.entityhub().delete(resourceId);
        assertTrue(removed);

        // Try to get the entity
        assertNull(client.entityhub().get(resourceId));

        // Test Get Entity Site
        Entity paris = client.entityhub().get("dbpedia", parisId);
        assertNotNull(paris);
        assertTrue(paris.getUri().equals(parisId));
        assertTrue(paris.getReferencedSite().equals("dbpedia"));
        assertTrue(paris.getCategories().get(1).equals("European Capitals of Culture"));
        assertTrue(paris.getPropertyValue("http://dbpedia.org/ontology/", "populationTotal").get(0).equals("2193031"));

        // Test Lookup
        paris = client.entityhub().lookup(parisId, true);
        entity = client.entityhub().get(paris.getUri());
        assertNotNull(entity);
        assertTrue(client.entityhub().delete(paris.getUri()));

        // Test Search
        LDPathProgram program = new LDPathProgram(ldPathProgram);
        List<Entity> entities = client.entityhub().search("Paris*", null, "en", program, 10, 0);
        assertTrue(entities.isEmpty());

        entities = client.entityhub().search("dbpedia", "Paris*", null, "en", program, 10, 0);
        assertTrue(!entities.isEmpty());
        assertTrue(entities.get(2).getPropertyValue("http://stanbol.apache.org/ontology/entityhub/find/", "labels").
                get(0).equals("Paris, France"));
        
        // Test ldpath
        program = new LDPathProgram();
        program.addNamespace("find", "http://stanbol.apache.org/ontology/entityhub/find/");
        program.addFieldDefinition("find:categories", "dc:subject :: xsd:anyURI;");

        Model model = client.entityhub().ldpath("dbpedia", parisId, program);
        String category = model.listObjectsOfProperty(model.getResource(parisId),
                model.createProperty(program.getNamespace("find"), "categories")).next().asResource().getURI();
        assertTrue(category.equals("http://dbpedia.org/resource/Category:Paris"));
    }
    
    @Test
    public void testSparql() throws Exception{
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        
        ContentHubDocumentRequest request = new ContentHubDocumentRequest();
        request.setTitle("TestSparql");
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE);
        request.setContentStream(stream);

        // add content
       String docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
       ResultSet results = client.sparql().executeQuery(StanbolSparqlService.ENHANCEMENT_GRAPH_URI, SPARQL_QUERY);
       while(results.hasNext()){
           QuerySolution solution = results.next();
           assertTrue(solution.contains("enhancement"));
           assertTrue(solution.contains("content"));
           assertTrue(solution.contains("engine"));
           assertTrue(solution.getResource("content").getURI().equals(docUri));
       }
       
       client.contenthub().delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);
    }
    
    @Test
    public void testLanguage() throws Exception
    {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);

        EnhancementResult enhancements = client.enhancer().enhance(TEST_URI,
                this.getClass().getClassLoader().getResourceAsStream(TEST_ES_FILE), "language");

        assertNotNull(enhancements);
        assertTrue(enhancements.getEnhancements().size() == 1);
        assertTrue(enhancements.getEnhancements().get(0) instanceof TextAnnotation);

        TextAnnotation annotation = (TextAnnotation) enhancements.getEnhancements().get(0);

        assertNotNull(annotation.getLanguage());
        assertTrue(annotation.getLanguage().equals("es"));
    }
}
