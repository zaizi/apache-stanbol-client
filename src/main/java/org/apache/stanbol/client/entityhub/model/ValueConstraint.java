package org.apache.stanbol.client.entityhub.model;

import java.util.List;

/**
 * 
 * @author rharo
 *
 */
public class ValueConstraint extends QueryConstraint
{
    /**
     * 
     */
    private List<String> values;
    
    /**
     * 
     */
    private String mode;
    
    /**
     * 
     */
    private String datatype;
    
    public ValueConstraint(String field, List<String> values, String mode, String datatype){
        this.field = field;
        this.type = Type.VALUE;
        this.mode = mode;
        this.values = values;
        this.datatype = datatype;
    }
    
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public List<String> getValues()
    {
        return values;
    }

    public void setValues(List<String> values)
    {
        this.values = values;
    }

    public String getDatatype()
    {
        return datatype;
    }

    public void setDatatype(String datatype)
    {
        this.datatype = datatype;
    }
   
}
