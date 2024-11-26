package com.emro.model;

import com.emro.contributor.SuffixTrie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class DictionaryCacheService {

    private final Project project;

    public DictionaryCacheService(Project project) throws IOException {
        this.project = project;

        // JSON 파일 로드
        Map<String, Map<String, String>> dictionary = loadJsonFile("dic.json");
        SuffixTrie trie = SuffixTrie.getInstance();

        // 데이터 삽입
        for (Map.Entry<String, Map<String, String>> entry : dictionary.entrySet()) {
            Map<String, String> metadata = entry.getValue();
            metadata.put("key", entry.getKey());
            trie.insert((String) metadata.get("ko"), metadata);
        }

    }

    // JSON 파일 로드
    private static Map<String, Map<String, String>> loadJsonFile(String fileName) {
        try {
            // resources 폴더에서 파일 로드
            InputStream inputStream = DictionaryCacheService.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }

            // JSON 파싱
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Error loading JSON file", e);
        }
    }

}
