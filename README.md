# Apache Stanbol Client

Apache Stanbol Client is a tool that let [Apache Stanbol](http://stanbol.apache.org/) integrators to use Apache Stanbol in an easy way. It covers almost the full REST API for the following Stanbol components:
**Enhancer**, **EntityHub** and **Sparql**.

Apache Stanbol Client can be distributed as a regular Java Library or can be integrated in your own project using Maven. The project is organized as a set of REST clients, one for each mentioned Stanbol Component. Each component Client has an implementation for all the RESTful services provided by the component API, managing the requests to the remote services and parsing service's responses for converting them to easy-to-use [POJOs](http://en.wikipedia.org/wiki/Plain_Old_Java_Object).


## Build From the Source

Current Built Requirements:

* Java 1.6+
* Maven 3.x

To start working with the project, just clone it in your local workspace:

    git clone https://github.com/zaizi/apache-stanbol-client.git

In order to use it into another Maven project, you need to install the library in your local Maven repository: 

    mvn clean install

The build process pass through a recommended test phase where some Unit tests are executed. These tests expects to find a local and clean Stanbol Server running. To ignore the tests, just add -DskipTests=true to the above maven command.

Once the library has been installed in your maven local repository, add the dependency in your pom file: 

    <dependency>
          <groupId>org.apache.stanbol</groupId>
          <artifactId>org.apache.stanbol.client</artifactId>
          <version>1.0-SNAPSHOT</version>
    </dependency>

To build a standalone library with all dependencies, run the following:

    mvn clean package

This will create a jar with the project dependencies in the target directory.

## How to Use

Below you can find some code examples showing part of the covered features for each Stanbol component. For a full specification of the Apache Stanbol Client API, consider explore the project [Javadoc]().

### [1. ENHANCER](http://stanbol.apache.org/docs/trunk/components/enhancer/)

#### Simple Content Enhancement

    final StanbolClient client = new StanbolClientImpl(STANBOL_ENDPOINT);
    EnhancementResult eRes = 
              client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
    
    for(TextAnnotation ta: eRes.getTextAnnotations()){
        System.out.println("********************************************");
        System.out.println("Selection Context: " + ta.getSelectionContext());
        System.out.println("Selected Text: " + ta.getSelectedText());
        System.out.println("Engine: " + ta.getCreator());
        System.out.println("Candidates: ");
        for(EntityAnnotation ea:eRes.getEntityAnnotations(ta))
              System.out.println("\t" + ea.getEntityLabel() + " - " + ea.getEntityReference());
    }

Produces:

    ********************************************
    Selection Context: Paris is the capital of France
    Selected Text: Paris
    Engine: org.apache.stanbol.enhancer.engines.opennlp.impl.NamedEntityExtractionEnhancementEngine
    Candidates: 
	    Paris-http://dbpedia.org/resource/Paris - 1.0
	    Paris, Texas-http://dbpedia.org/resource/Paris,_Texas - 0.17877833090589348
    ********************************************
    Selection Context: Paris is the capital of France
    Selected Text: France
    Engine: org.apache.stanbol.enhancer.engines.opennlp.impl.NamedEntityExtractionEnhancementEngine
    Candidates: 
	    France-http://dbpedia.org/resource/France - 1.0
	    New France-http://dbpedia.org/resource/New_France - 0.2348975694560165
	    Vichy France-http://dbpedia.org/resource/Vichy_France - 0.19574797454668041
    ********************************************

#### Local Disambiguation

    EnhancementResult eRes = 
        client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
    eRes.disambiguate();

This piece of code removes all Entity Annotations from the results with confidence value less than the highest candidate's value, therefore it would print the following:

    ********************************************
    Selection Context: Paris is the capital of France
    Selected Text: France
    Engine: org.apache.stanbol.enhancer.engines.opennlp.impl.NamedEntityExtractionEnhancementEngine
    Candidates: 
	    France - http://dbpedia.org/resource/France - 1.0
    ********************************************
    Selection Context: Paris is the capital of France
    Selected Text: Paris
    Engine: org.apache.stanbol.enhancer.engines.opennlp.impl.NamedEntityExtractionEnhancementEngine
    Candidates: 
    	Paris - http://dbpedia.org/resource/Paris - 1.0
    ********************************************

#### Filter By Confidence

    EnhancementResult eRes = 
        client.enhancer().enhance(TEST_URI, "Paris is the capital of France");
    assertTrue(eRes.getEntityAnnotations().size() == 5);
    eRes.filterByConfidence(0.2);
    assertTrue(eRes.getEntityAnnotations().size() == 3);

#### Enhance a File or InputStream with any Enhancement Engine

    EnhancementResult eRes = 
        client.enhancer().enhance(TEST_URI,
        this.getClass().getClassLoader().getResourceAsStream(TEST_EN_FILE), "language");
    
    assertTrue(eRes.getEnhancements().size() == 1);
    TextAnnotation annotation = (TextAnnotation) eRes.getEnhancements().get(0);
    assertTrue(annotation.getLanguage().equals("en"));
   


### [3. ENTITYHUB](http://stanbol.apache.org/docs/trunk/components/entityhub/)

#### Entity CRUD

    // Create
    final String resourceId = "http://dbpedia.org/resource/Doctor_Who";
    String id = client.entityhub().
           create(this.getClass().getClassLoader().getResourceAsStream(TEST_RDF_FILE),
                resourceId, true);

    assertNotNull(id);
    assertTrue(id.toString().indexOf(resourceId) != -1);

    // Retrieve
    Entity entity = client.entityhub().get(resourceId);
    assertNotNull(entity);
    assertTrue(entity.getLabels("en").get(0).equals("Doctor Who"));
    assertTrue(entity.getUri().equals(resourceId));
    assertTrue(entity.getReferencedSite().equals("entityhub"));
    assertTrue(entity.getCategories().get(0).equals("BBC television programmes"));
    assertTrue(entity.getTypes().get(0).equals("Creative Work"));
    assertTrue(entity.getPropertyValue("http://dbpedia.org/property/", 
                                           "numEpisodes").get(0).equals("777"));

    // Delete
    boolean removed = client.entityhub().delete(resourceId);
    assertTrue(removed);

#### Entity Search

    final String ldPathProgram = 
        "@prefix find:<http://stanbol.apache.org/ontology/entityhub/find/>; 
         find:labels = rdfs:label[@en] :: xsd:string; 
         find:comment = rdfs:comment[@en] :: xsd:string; 
         find:categories = dc:subject :: xsd:anyURI; 
         find:mainType = rdf:type :: xsd:anyURI;";

    // Search in DBPedia Referenced Site
    LDPathProgram program = new LDPathProgram(ldPathProgram);
    List<Entity> entities = client.entityhub().search("dbpedia", "Paris*", 
                                                     null, "en", program, 10, 0);

     assertTrue(!entities.isEmpty());
     assertTrue(entities.get(2).getPropertyValue(
         "http://stanbol.apache.org/ontology/entityhub/find/", "labels").
                                                   get(0).equals("Paris, France"));


## License

Apache Stanbol Client is distributed under the terms of the [Apache License, 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). For enterprise support, please contact us at [Zaizi Ltd](http://www.zaizi.com).
