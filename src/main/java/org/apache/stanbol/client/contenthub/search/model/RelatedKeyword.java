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
package org.apache.stanbol.client.contenthub.search.model;

/**
 * Representation of a Related Keyword as it is currently modeled in Stanbol. A RelatedKeyword is a concept related to a initial Search Keyword extracted from a Semantic Source that
 * currently can be WordNet or an Ontology within Stanbol. Each RelatedKeyword has a relatedness score with the search keyword.  
 * 
 * @author Rafa Haro
 * 
 */
public class RelatedKeyword
{

    /**
     * To enumerate the sources for a related keyword
     */
    public enum Source
    {

        UNKNOWN("Unknown"),

        WORDNET("Wordnet"),

        ONTOLOGY("Ontology");

        private final String name;

        private Source(String n)
        {
            this.name = n;
        }

        @Override
        public final String toString()
        {
            return this.name;
        }
    }

    private String keyword;
    private double score;
    private String source;

    public RelatedKeyword(String keyword, double score)
    {
        this.keyword = keyword;
        this.score = score;
        this.source = RelatedKeyword.Source.UNKNOWN.toString();
    }

    public RelatedKeyword(String keyword, double score, String source)
    {
        this.keyword = keyword;
        this.score = score;
        this.source = source;
    }

    public RelatedKeyword(String keyword, double score, RelatedKeyword.Source source)
    {
        this.keyword = keyword;
        this.score = score;
        this.source = source.toString();
    }

    public String getKeyword()
    {
        return this.keyword;
    }

    public double getScore()
    {
        return this.score;
    }

    public String getSource()
    {
        return this.source;
    }
}
