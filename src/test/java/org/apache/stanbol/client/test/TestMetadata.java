package org.apache.stanbol.client.test;

import java.util.List;

import org.apache.stanbol.client.contenthub.store.model.Metadata;

public class TestMetadata extends Metadata
{
    public TestMetadata(String name, List<String> value)
    {
        this.name = name;
        this.value = value;
        this.namespace = "http://org.apache.stanbol/testmetadata/";
        this.type = "CustomMetadata";
        this.creator = "JUNIT";
    }
}
