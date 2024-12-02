package com.emro.dictionary;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.util.CharTokenizer;

// n-gram 분석기 정의
public class NGramAnalyzer extends Analyzer {
    private final int minGram;
    private final int maxGram;

    public NGramAnalyzer(int minGram, int maxGram) {
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // 공백을 포함하여 토큰화하는 Custom Tokenizer
        Tokenizer tokenizer = new CharTokenizer() {
            @Override
            protected boolean isTokenChar(int c) {
                return true; // 모든 문자를 토큰화
            }
        };
        TokenStream tokenStream = new NGramTokenFilter(tokenizer, minGram, maxGram, false); // n-gram 생성
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
