package com.emro.dictionary;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuceneManager {

	// 싱글톤 인스턴스
	private static LuceneManager instance;

	private final Directory directory;
	private final Analyzer analyzer;
	private final IndexWriter writer;
	private DirectoryReader reader;

	// private 생성자 (싱글톤)
	private LuceneManager() throws IOException {
		this.directory = new ByteBuffersDirectory();
		this.analyzer = new NGramAnalyzer(1, 2);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		this.writer = new IndexWriter(directory, config);
	}

	// 싱글톤 인스턴스 반환
	public static synchronized LuceneManager getInstance() throws IOException {
		if (instance == null) {
			instance = new LuceneManager();
		}
		return instance;
	}

	// JSON 데이터를 색인
	public void indexData(Map<String, Object> record) throws Exception {
		Document doc = new Document();
		doc.add(new TextField("key", record.get("key").toString(), Field.Store.YES));
		doc.add(new TextField("en", record.get("en").toString(), Field.Store.YES));
		doc.add(new TextField("ko", record.get("ko").toString(), Field.Store.YES));
		doc.add(new TextField("source", record.get("source").toString(), Field.Store.YES));
		writer.addDocument(doc);
		writer.commit(); // 변경 사항 저장
	}

	// 검색
	public List<Map<String, Object>> search(String field, String queryText, String type) throws Exception {
		if (reader == null || !reader.isCurrent()) {
			if (reader != null) reader.close();
			reader = DirectoryReader.open(directory);
		}
		IndexSearcher searcher = new IndexSearcher(reader);

		Query query;
		try {
			query = DynamicAnalyzerSearch.createQuery(field, queryText, type);
		} catch (Exception ex) {
			throw new RuntimeException("쿼리 생성 실패: " + ex.getMessage(), ex);
		}

		TopDocs results = searcher.search(query, Integer.MAX_VALUE);
		List<Map<String, Object>> searchResults = new ArrayList<>();

		for (ScoreDoc scoreDoc : results.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			searchResults.add(Map.of(
					"key", doc.get("key"),
					"en", doc.get("en"),
					"ko", doc.get("ko"),
					"source", doc.get("source")
			));
		}
		return searchResults;
	}

	// 자원 정리
	public void close() throws IOException {
		if (writer != null) {
			writer.close();
		}
		if (reader != null) {
			reader.close();
		}
		directory.close();
	}



}
