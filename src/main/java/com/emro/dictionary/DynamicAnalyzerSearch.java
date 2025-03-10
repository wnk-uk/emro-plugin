package com.emro.dictionary;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.HashMap;
import java.util.Map;

public class DynamicAnalyzerSearch {

    // Analyzer 매핑
    private static final Map<String, Analyzer> analyzers = new HashMap<>();

    static {
        analyzers.put("keyword", new KeywordAnalyzer());
        analyzers.put("standard", new StandardAnalyzer());
        analyzers.put("ngram", new NGramAnalyzer(1, 2));
	    analyzers.put("ngram2", new NGramAnalyzer(2, 3));
    }

    // 특정 Analyzer로 검색
    public static Query createQuery(String field, String queryText, String analyzerType) throws ParseException {
        Analyzer analyzer = analyzers.getOrDefault(analyzerType, new StandardAnalyzer());
        QueryParser parser = new QueryParser(field, analyzer);
        return parser.parse(queryText);
    }
}
