package org.apache.stanbol.client.entityhub.model;

/**
 * 
 * @author Rafa Haro
 *
 */
public abstract class QueryConstraint
{
    public enum Type{
        REFERENCE, VALUE, TEXT, RANGE, SIMILARITY;
    };
    
    protected String field;
      
    protected Type type;

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public Type getType()
    {
        return type;
    }
}
