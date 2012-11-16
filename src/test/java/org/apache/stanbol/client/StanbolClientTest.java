package org.apache.stanbol.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.apache.stanbol.client.impl.StanbolClientImpl;
import org.apache.stanbol.client.model.ContentHubDocument;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.model.Entity;
import org.apache.stanbol.client.model.LDPathProgram;
import org.apache.stanbol.client.model.TextAnnotation;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class StanbolClientTest {

    private static final String STANBOL_ENDPOINT = "http://localhost:9080/";
    private static final String TEST_EN_FILE = "test_en.txt";
    private static final String TEST_ES_FILE = "test_es.txt";
    private static final String TEST_RDF_FILE = "Doctor_Who.txt";

    @Test
    public void testClient() throws Exception {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);

        List<Enhancement> enhancements = client.enhancer().enhance(this.getClass().getClassLoader()
                .getResourceAsStream(TEST_EN_FILE));
        assertNotNull(enhancements);
        assertFalse(enhancements.size() == 0);
    }

    @Test
    public void testContentHub() throws Exception {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);

        // contenthub request
        ContentHubDocument request = new ContentHubDocument();
        request.setId("test1");
        request.setTitle("Test 1");
        request.setContentStream(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE));
        
        // add content
        client.contenthub().add(request);
        
        // load content
        ContentItem ci = client.contenthub().get(request);
        
        // checks
        assertNotNull(ci);
        assertFalse(ci.getEnhancements().size() == 0);
        assertTrue(ci.getId().equals(request.getId()));
    }

    @Test
    public void testEntiyHub() throws Exception {
        
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
        final URI resourceId = new URI("http://dbpedia.org/resource/Doctor_Who");
        final URI parisId = new URI("http://dbpedia.org/resource/Paris");
        final String ldPathProgram = "@prefix find:<http://stanbol.apache.org/ontology/entityhub/find/>; find:labels = rdfs:label[@en] :: xsd:string; find:comment = rdfs:comment[@en] :: xsd:string; find:categories = dc:subject :: xsd:anyURI; find:mainType = rdf:type :: xsd:anyURI;";
                
        // Create the entity
        URI id = client.entityhub().create(this.getClass().getClassLoader().getResourceAsStream(TEST_RDF_FILE), resourceId, true);
        assertNotNull(id);
        assertTrue(id.toString().indexOf(resourceId.toString())!=-1);

        // Get the entity
        Entity entity = client.entityhub().get(resourceId);
        assertNotNull(entity);
        assertTrue(entity.getUri().equals(resourceId.toString()));
        
        // Test Entity Model
        assertTrue(entity.getReferencedSite().equals("entityhub"));
        assertTrue(entity.getCategories().get(0).equals("BBC television programmes"));
        assertTrue(entity.getTypes().get(0).equals("Creative Work"));
        assertNotNull(entity.getComment());
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
        assertTrue(paris.getUri().equals(parisId.toString()));
        assertTrue(paris.getReferencedSite().equals("dbpedia"));
        assertTrue(paris.getCategories().get(1).equals("European Capitals of Culture"));
        assertTrue(paris.getPropertyValue("http://dbpedia.org/ontology/", "populationTotal").get(0).equals("2193031"));
        
        // Test Lookup
        paris = client.entityhub().lookup(parisId.toString(), true);
        entity = client.entityhub().get(new URI(paris.getUri()));
        assertNotNull(entity);
        assertTrue(client.entityhub().delete(new URI(paris.getUri())));
        
        // Test Search
        LDPathProgram program = new LDPathProgram(ldPathProgram);
        List<Entity> entities = client.entityhub().search("Paris*", null, "en", program, 10, 0);
        assertTrue(entities.isEmpty());
        
        entities = client.entityhub().search("dbpedia", "Paris*", null, "en", program, 10, 0);
        assertTrue(!entities.isEmpty());
        assertTrue(entities.get(0).getPropertyValue("http://stanbol.apache.org/ontology/entityhub/find/", "labels").get(0).equals("University of Paris alumni"));
        
        // Test ldpath
        program = new LDPathProgram();
        program.addNamespace("find", "http://stanbol.apache.org/ontology/entityhub/find/");
        program.addFieldDefinition("find:categories", "dc:subject :: xsd:anyURI;");
        
        Model model = client.entityhub().ldpath("dbpedia", parisId, program);
        String category = model.listObjectsOfProperty(model.getResource(parisId.toString()), model.createProperty(program.getNamespace("find"), "categories")).next().asResource().getURI();
        assertTrue(category.equals("http://dbpedia.org/resource/Category:Paris"));  
    }

    @Test
    public void testLanguage() throws Exception {
        final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);

        List<Enhancement> enhancements = client.enhancer().enhance(this.getClass().getClassLoader().getResourceAsStream(TEST_ES_FILE), "language");
        
        assertNotNull(enhancements);
        assertTrue(enhancements.size() == 1);
        assertTrue(enhancements.get(0) instanceof TextAnnotation);
        
        TextAnnotation annotation = (TextAnnotation)enhancements.get(0);
        
        assertNotNull(annotation.getLanguage());
        assertTrue(annotation.getLanguage().equals("es"));
    }
}
