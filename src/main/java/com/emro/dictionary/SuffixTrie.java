package com.emro.dictionary;

import java.util.*;

public class SuffixTrie {
    private final SuffixTrieNode root = new SuffixTrieNode();

    private SuffixTrie() {}

    // Static inner class for lazy-loading
    private static class SuffixTrieHolder {
        private static final SuffixTrie INSTANCE = new SuffixTrie();
    }

    // Public method to get the singleton instance
    public static SuffixTrie getInstance() {
        return SuffixTrieHolder.INSTANCE;
    }

    // 단어와 메타데이터 삽입
    public void insert(String word, Map<String, String> metadata) {
        if (word == null || word.isEmpty() || metadata == null || metadata.isEmpty()) {
            throw new IllegalArgumentException("Word and metadata cannot be null or empty");
        }

        SuffixTrieNode node = root;

        // 단어의 모든 접미사 처리
        for (int i = 0; i < word.length(); i++) {
            for (int j = i; j < word.length(); j++) {
                char c = word.charAt(j);
                node = node.children.computeIfAbsent(c, k -> new SuffixTrieNode());
            }

            // 단어 끝 노드에만 메타데이터 저장
            if (!node.words.contains(word)) {
                node.words.add(word);
                node.wordMetadata.put(word, new HashMap<>(metadata)); // 단어별로 정확한 메타데이터 저장
            }
        }
    }

    // 부분 문자열 검색
    public List<Map<String, Object>> search(String substring) {
        if (substring == null || substring.isEmpty()) {
            return Collections.emptyList();
        }

        SuffixTrieNode node = root;
        for (char c : substring.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return Collections.emptyList(); // 매칭되는 접두사가 없을 경우
            }
        }

        // 현재 노드와 자식 노드의 데이터를 수집
        List<Map<String, Object>> results = new ArrayList<>();
        collectMetadata(node, results);
        return results;
    }

    // Helper method to collect metadata from a node and its children
    private void collectMetadata(SuffixTrieNode node, List<Map<String, Object>> results) {
        for (String word : node.words) {
            Map<String, Object> result = new HashMap<>();
            result.put("ko", word);
            result.put("metadata", new HashMap<>(node.wordMetadata.get(word))); // 단어에 해당하는 메타데이터만 반환
            results.add(result);
        }

        for (SuffixTrieNode child : node.children.values()) {
            collectMetadata(child, results); // 재귀적으로 자식 노드 탐색
        }
    }

    static class SuffixTrieNode {
        Map<Character, SuffixTrieNode> children = new HashMap<>(); // 자식 노드
        Set<String> words = new HashSet<>(); // 끝 노드에 저장된 단어
        Map<String, Map<String, String>> wordMetadata = new HashMap<>(); // 단어별 메타데이터 저장

        public SuffixTrieNode() {}
    }
}
