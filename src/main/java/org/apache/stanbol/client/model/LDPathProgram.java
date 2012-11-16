package org.apache.stanbol.client.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.stanbol.client.exception.StanbolClientException;

/**
 * 
 * @author rharo
 *
 */
public class LDPathProgram
{
    private static final Pattern prefixPattern = Pattern.compile("@prefix\\s*(\\S*)((\\s*:\\s*)|(:\\s*))<(\\S*)>(\\s*)?;");
    private static final Pattern fieldPattern = Pattern.compile("(\\S*)\\s*=(([^;]*));");
       
    private Map<String, String> namespaces;
    
    private Map<String, String> fields;
    
    public LDPathProgram(){
        namespaces = new HashMap<String, String>();
        fields = new HashMap<String, String>();
    }
    
    public LDPathProgram(String ldPathProgram) throws StanbolClientException{
        this();
        
        // Parameter Parsing; 
        Matcher matcher = prefixPattern.matcher(ldPathProgram);
        while(matcher.find()){
            String prefix = matcher.group(1);
            if(prefix == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Prefix Definition Error");
            
            String namespace = matcher.group(5);
            if(namespace == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Namespace Definition Error");
            
            namespaces.put(prefix, namespace);
        }
        
        String restProgram = ldPathProgram;
        if(namespaces.size() > 0){
            int prefix = ldPathProgram.lastIndexOf("@prefix");
            String rest = ldPathProgram.substring(prefix);
            restProgram = rest.substring(rest.indexOf(';')+1);
        }
                
        matcher = fieldPattern.matcher(restProgram);
        
        
        while(matcher.find()){
            String fieldName = matcher.group(1);
            if(fieldName == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Field Name Definition Error");
            
            String fieldDefinition = matcher.group(2);
            if(fieldDefinition == null)
                throw new StanbolClientException("LDPath Program Sintax Error. Field Definition Error");
            
            fields.put(fieldName, fieldDefinition);
        }
    }
    
    public void addNamespace(String prefix, String namespace){
        if(!namespaces.containsValue(namespace))
            namespaces.put(prefix, namespace);
    }
    
    public void addFieldDefinition(String fieldName, String fieldDefinition){
        if(!fields.containsValue(fieldDefinition))
            fields.put(fieldName, fieldDefinition);
    }
    
    public String getNamespace(String prefix){
        return namespaces.get(prefix);
    }
    
    public String getFieldDefinition(String fieldName){
        return fields.get(fieldName);
    }
    
    public String getPrefix(String namespace){
        Iterator<Entry<String, String>> it = namespaces.entrySet().iterator();
        while(it.hasNext()){
            Entry<String, String> next = it.next();
            if(next.getValue().equals(namespace))
                return next.getKey();
        }
        
        return null;
    }
    
    public String toString(){
        String result = new String();
        
        //Prefixes
        Iterator<String> it = namespaces.keySet().iterator();
        while(it.hasNext()){
            String nextPrefix = it.next();
            result += "@prefix " + nextPrefix + ":<" + namespaces.get(nextPrefix) + ">;";
        }
        
        it = fields.keySet().iterator();
        while(it.hasNext()){
            String nextField = it.next();
            result += nextField + " = " + fields.get(nextField);
            if(!fields.get(nextField).endsWith(";"))
                result += ";";
        }    
        
        return result;
    }
    
}
