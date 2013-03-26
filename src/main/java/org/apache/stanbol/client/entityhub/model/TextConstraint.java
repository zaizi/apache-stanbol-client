package org.apache.stanbol.client.entityhub.model;

import java.util.List;

/**
 * 
 * @author rharo
 *
 */
public class TextConstraint extends QueryConstraint
{
    public enum PatternType{
      WILDCARD("wildcard"), REGEX("regex"), NONE("none");
      
      private final String pattern;
      
      private PatternType(final String pattern){
          this.pattern = pattern;
      }
      
      public String toString(){
          return pattern;
      }
      
    }
    
    private List<String> searchText;
    
    private List<String> languages;
    
    private PatternType patternType;
    
    private boolean caseSensitive;

    public TextConstraint(String field, List<String> searchText, List<String> languages, PatternType patternType,
            boolean caseSensitive)
    {
        this.type = Type.TEXT;
        this.field = field;
        this.searchText = searchText;
        this.languages = languages;
        this.patternType = patternType;
        this.caseSensitive = caseSensitive;
    }

    public List<String> getSearchText()
    {
        return searchText;
    }

    public void setSearchText(List<String> searchText)
    {
        this.searchText = searchText;
    }

    public List<String> getLanguages()
    {
        return languages;
    }

    public void setLanguages(List<String> languages)
    {
        this.languages = languages;
    }

    public PatternType getPatternType()
    {
        return patternType;
    }

    public void setPatternType(PatternType patternType)
    {
        this.patternType = patternType;
    }

    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }
    
    
    
}
