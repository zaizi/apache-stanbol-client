package org.apache.stanbol.client.entityhub.model;

import java.util.List;

/**
 * 
 * 
 * @author rharo
 *
 */
public class ReferenceConstraint extends QueryConstraint
{   
    /**
     * 
     */
    private List<String> values;
    
    /**
     * 
     */
    private String mode;
    
    public ReferenceConstraint(String field, List<String> values, String mode){
        this.field = field;
        this.type = Type.REFERENCE;
        this.mode = mode;
        this.values = values;
    }
    
    public List<String> getValues()
    {
        return values;
    }

    public void setValues(List<String> uriValues)
    {
        this.values = uriValues;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }
    
}
