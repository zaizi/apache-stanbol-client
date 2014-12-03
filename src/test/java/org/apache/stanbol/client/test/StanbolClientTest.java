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
package org.apache.stanbol.client.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.stanbol.client.Enhancer;
import org.apache.stanbol.client.EntityHub;
import org.apache.stanbol.client.StanbolClientFactory;
import org.apache.stanbol.client.enhancer.impl.EnhancerParameters;
import org.apache.stanbol.client.enhancer.model.EnhancementStructure;
import org.apache.stanbol.client.enhancer.model.EntityAnnotation;
import org.apache.stanbol.client.enhancer.model.TextAnnotation;
import org.apache.stanbol.client.entityhub.model.Entity;
import org.apache.stanbol.client.entityhub.model.LDPathProgram;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDFS;

public class StanbolClientTest
{
	private static StanbolClientFactory factory;

    private static final String STANBOL_ENDPOINT = "http://localhost:9090/";
    
    private static final String TEST_EN_FILE = "test_en.txt";
//    private static final String TEST_EN2_FILE = "test_en2.txt";
    private static final String TEST_ES_FILE = "test_es.txt";
    private static final String TEST_RDF_FILE = "Doctor_Who.txt";
    private static final String TEST_SENTENCE = "Paris is the capital of France";
    
//    private static final String SPARQL_QUERY = "PREFIX fise: <http://fise.iks-project.eu/ontology/>" +
//    		"PREFIX dc:   <http://purl.org/dc/terms/>" +
//    		"SELECT distinct ?enhancement ?content ?engine ?extraction_time" +
//    		"WHERE {" +
//    		"  ?enhancement a fise:TextAnnotation ." +
//    		"  ?enhancement fise:extracted-from ?content ." +
//    		"  ?enhancement dc:creator ?engine ." +
//    		"  ?enhancement dc:created ?extraction_time ." +
//    		"}" +
//    		"ORDER BY DESC(?extraction_time) LIMIT 5";
    
    @BeforeClass
    public static void startClient(){
    	factory = new StanbolClientFactory(STANBOL_ENDPOINT);
    }
    
    @Test
    public void testEnhancerBasic() throws Exception
    {
        final Enhancer client = factory.createEnhancerClient();
        EnhancerParameters parameters = EnhancerParameters.
    			builder().
    			buildDefault(TEST_SENTENCE);

        EnhancementStructure eRes = client.enhance(parameters);
        
        Assert.assertTrue(eRes.getEntityAnnotations().size() == 6);
        eRes.filterByConfidence(0.2);
        Assert.assertTrue(eRes.getEntityAnnotations().size() == 5);

        EnhancementStructure enhancements = client.enhance(parameters);
        Assert.assertNotNull(enhancements);
        Assert.assertFalse(enhancements.getEnhancements().size() == 0);
        Assert.assertTrue(enhancements.getEntityAnnotations().size() == 6);
        
        Assert.assertEquals(enhancements.getEntityAnnotations().iterator().next().getSite(), "dbpedia");
        
        List<String> labels = Lists.newArrayList();
        for(EntityAnnotation ea:enhancements.getEntityAnnotations())
            labels.add(ea.getEntityLabel());
        
        Assert.assertTrue(labels.contains("Paris"));
        Assert.assertTrue(labels.contains("France"));
        
        
        enhancements.filterByConfidence(0.6);
        for(TextAnnotation ta:enhancements.getTextAnnotations()){
            Collection<EntityAnnotation> eas = enhancements.getEntityAnnotations(ta);
            if(eas.size() > 0)
                Assert.assertTrue(eas.size() == 1);
        }
        
        parameters = EnhancerParameters.
    			builder().
    			setContent(this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE)).
    			build();
        
        enhancements = client.enhance(parameters);
        Assert.assertNotNull(enhancements);
        Assert.assertFalse(enhancements.getEnhancements().size() == 0);
    }
    
    @Test
    public void testEnhancerAdvanced() throws StanbolServiceException, JSONException{
    	final Enhancer client = factory.createEnhancerClient();
        EnhancerParameters parameters = EnhancerParameters.
    			builder().
    			buildDefault(TEST_SENTENCE);

        EnhancementStructure eRes = client.enhance(parameters);
        
        Multimap<TextAnnotation, EntityAnnotation> bests = eRes.getBestAnnotations();
        Collection<TextAnnotation> tas = bests.keySet();
        TextAnnotation paris = assertBest("Paris", tas);        		
        Assert.assertNotNull(paris);
        Collection<EntityAnnotation> bestEas = bests.get(paris);
        Assert.assertEquals(bestEas.size(), 1);
        Assert.assertEquals(bestEas.iterator().next().getEntityReference(), "http://dbpedia.org/resource/Paris");
        TextAnnotation france = assertBest("France", tas);
        Assert.assertNotNull(france);
        bestEas = bests.get(france);
        Assert.assertEquals(bestEas.size(), 1);
        Assert.assertEquals(bestEas.iterator().next().getEntityReference(), "http://dbpedia.org/resource/France");
        
        // Sizing
        Assert.assertEquals(eRes.getEnhancements().size(), 9);
        Assert.assertEquals(eRes.getEntities().size(), 6);
        Assert.assertFalse(eRes.getEnhancementGraph().isEmpty());
        
        // Entity
        Assert.assertFalse(eRes.getEntities().isEmpty());
        Entity eParis = eRes.getEntity("http://dbpedia.org/resource/Paris");
        Assert.assertNotNull(paris);
        Assert.assertFalse(eParis.getProperties().isEmpty());

                
        Assert.assertFalse(eParis.getPropertyValues(RDFS.label).isEmpty());
        Assert.assertTrue(eParis.getLabels("en").contains("Paris"));
        Assert.assertEquals(eParis.getReferencedSite(), "dbpedia");
        Assert.assertTrue(eParis.getCategories().isEmpty());
        Assert.assertTrue(eParis.getTypes().contains("http://dbpedia.org/ontology/Place"));
        
        
        Assert.assertTrue(Float.parseFloat(
        		eParis.getPropertyValues("http://www.w3.org/2003/01/geo/wgs84_pos#lat").
        		iterator().next()) == 48.8567f);
        Assert.assertTrue(Float.parseFloat(
        		eParis.getPropertyValues("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat").
        		iterator().next()) == 48.8567f);
        
        Map<String, String> values = eParis.getPropertyValuesByLanguage(RDFS.label);
        Assert.assertEquals(values.get("it"), "Parigi");
        Assert.assertEquals(values.get("ru"), "Париж");
        
        values = eParis.getPropertyValuesByLanguage(RDFS.label.getURI());
        Assert.assertEquals(values.get("en"), "Paris");
        Assert.assertEquals(values.get("es"), "París");

        EntityAnnotation parisEa = eRes.getEntityAnnotation(eParis.getUri());
        Assert.assertNotNull(parisEa);
        Assert.assertTrue(parisEa.getEntityTypes().contains("http://dbpedia.org/ontology/Place"));
        Assert.assertEquals("Paris", parisEa.getEntityLabel());
        Assert.assertEquals("dbpedia", parisEa.getSite());
        
        String jsonEnh = eRes.toJSONString();
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(jsonEnh);
//        String prettyJsonString = gson.toJson(je);
//        System.out.println(prettyJsonString);
        JSONObject json = new JSONObject(jsonEnh);
        JSONArray array = json.getJSONArray("annotations");
        Assert.assertEquals(array.getJSONObject(1).getString("start"), "24");
        Assert.assertEquals(array.getJSONObject(1).getString("end"), "30");
        
    }
    
    private TextAnnotation assertBest(final String reference, Collection<TextAnnotation> tas){
    	return FluentIterable.
        		from(tas).
        		firstMatch(new Predicate<TextAnnotation>() {
					@Override
					public boolean apply(TextAnnotation input) {
						return input.getSelectedText().equals(reference);
					}
				}).orNull();
    }

    @Test
    public void testEntityHub() throws Exception
    {
        final EntityHub client = factory.createEntityHubClient();
        final String resourceId = "http://dbpedia.org/resource/Doctor_Who";
        final String parisId = "http://dbpedia.org/resource/Paris";
        final String ldPathProgram = "@prefix find:<http://stanbol.apache.org/ontology/entityhub/find/>; find:labels = rdfs:label[@en] :: xsd:string; find:comment = rdfs:comment[@en] :: xsd:string; find:categories = dc:subject :: xsd:anyURI; find:mainType = rdf:type :: xsd:anyURI;";

        // Create the entity
        String id = client.create(this.getClass().getClassLoader().getResourceAsStream(TEST_RDF_FILE),
                resourceId, true);
        Assert.assertNotNull(id);
        Assert.assertNotEquals(id.toString().indexOf(resourceId), -1);

        // Get the entity
        Entity entity = client.get(resourceId);
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getLabels("en").iterator().next(), "Doctor Who");
        Assert.assertEquals(entity.getUri(), resourceId);

        // Test Entity Model
        Assert.assertEquals(entity.getReferencedSite(), "entityhub");
        Assert.assertEquals(entity.getCategories().iterator().next(), 
        		"http://dbpedia.org/resource/Category:BBC_television_programmes");
        Assert.assertEquals(entity.getTypes().iterator().next(), "http://schema.org/CreativeWork");
        Assert.assertNotNull(entity.getComments("en").iterator().next());
        
        Collection<String> labels = entity.getLabels("en");
        Assert.assertEquals(labels.size(), 1);
        Assert.assertEquals(entity.getLabels("en").iterator().next(), "Doctor Who");
        Assert.assertEquals(entity.
        		getPropertyValues("http://dbpedia.org/property/", "numEpisodes").
        		iterator().next(), "777");

        // Remove the entity
        boolean removed = client.delete(resourceId);
        Assert.assertTrue(removed);

        // Try to get the entity
        Assert.assertNull(client.get(resourceId));

        // Test Get Entity Site
        Entity paris = client.get("dbpedia", parisId);
        Assert.assertNotNull(paris);
        Assert.assertEquals(paris.getUri(), parisId);
        Assert.assertEquals(paris.getReferencedSite(), "dbpedia");
        Assert.assertEquals(CollectionUtils.get(paris.getCategories(), 1), "http://dbpedia.org/resource/Category:3rd-century_BC_establishments");
        Assert.assertEquals(paris.
        		getPropertyValues("http://dbpedia.org/ontology/", 
        				"populationTotal").iterator().next(), "2211297");

        // Test Lookup
        paris = client.lookup(parisId, true);
        entity = client.get(paris.getUri());
        Assert.assertNotNull(entity);
        Assert.assertTrue(client.delete(paris.getUri()));

        // Test Search
        LDPathProgram program = new LDPathProgram(ldPathProgram);
        Collection<Entity> entities = client.search("Paris*", null, "en", program, 10, 0);
        Assert.assertTrue(entities.isEmpty());

        entities = client.search("dbpedia", "Paris*", null, "en", program, 10, 0);
        Assert.assertFalse(entities.isEmpty());
        List<Entity> eList = Lists.newArrayList(entities);
        assertEquals(eList.get(2).
        		getPropertyValues("http://stanbol.apache.org/ontology/entityhub/find/", "labels").
                iterator().next(), "Civil parishes in England");
        
        // Test ldpath
        program = new LDPathProgram();
        program.addNamespace("find", "http://stanbol.apache.org/ontology/entityhub/find/");
        program.addFieldDefinition("find:categories", "dc:subject :: xsd:anyURI;");

        Model model = client.ldpath("dbpedia", parisId, program);
        String category = model.listObjectsOfProperty(model.getResource(parisId),
                model.createProperty(program.getNamespace("find"), "categories")).next().asResource().getURI();
        Assert.assertEquals(category, "http://dbpedia.org/resource/Category:Paris");
    }
    
//    @Test
//    public void testSparql() throws Exception{
//        final StanbolClientFactory client = new StanbolClientImpl(STANBOL_ENDPOINT);
//        
//        ContentHubDocumentRequest request = new ContentHubDocumentRequest();
//        request.setTitle("TestSparql");
//        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE);
//        request.setContentStream(stream);
//
//        // add content
//       String docUri = client.contenthub().add(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, "default", request);
//       ResultSet results = client.sparql().executeQuery(Sparql.ENHANCEMENT_GRAPH_URI, SPARQL_QUERY);
//       while(results.hasNext()){
//           QuerySolution solution = results.next();
//           assertTrue(solution.contains("enhancement"));
//           assertTrue(solution.contains("content"));
//           assertTrue(solution.contains("engine"));
//           assertTrue(solution.getResource("content").getURI().equals(docUri));
//       }
//       
//       client.contenthub().delete(StanbolContenthubStoreService.STANBOL_DEFAULT_INDEX, docUri);
//    }
    
    @Test
    public void testLanguage() throws Exception
    {
    	final Enhancer client = factory.createEnhancerClient();
    	EnhancerParameters parameters = EnhancerParameters.
    			builder().
    			setChain("language").
    			setContent(this.getClass().getClassLoader().getResourceAsStream(TEST_ES_FILE)).
    			build();
        EnhancementStructure enhancements = client.enhance(parameters);

        Assert.assertNotNull(enhancements);
        Assert.assertTrue(enhancements.getEnhancements().size() == 1);
        TextAnnotation annotation = enhancements.getTextAnnotations().iterator().next();
        Assert.assertTrue(annotation instanceof TextAnnotation);

        Assert.assertNotNull(annotation.getLanguage());
        Assert.assertEquals(annotation.getLanguage(), "es");
        
        Assert.assertFalse(enhancements.getLanguages().isEmpty());
        Assert.assertEquals(enhancements.getLanguages().size(), 1);
        Assert.assertEquals(enhancements.getLanguages().iterator().next(), "es");
    }
}
