package com.emro.dictionary;

import com.emro.configuration.PluginSettingsState;
import com.emro.configuration.ProjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class DictionaryCacheService {

    private final Project project;
    private static Map<String, Map<String, Object>> dictionary;

    public DictionaryCacheService(Project project) throws Exception {
        this.project = project;

        // JSON 파일 로드
        if (dictionary == null) {
            dictionary = loadJsonFile("dic.json");
        }

        // 데이터 삽입
        for (Map.Entry<String, Map<String, Object>> entry : dictionary.entrySet()) {
            Map<String, Object> metadata = entry.getValue();
            metadata.put("key", entry.getKey());
	        LuceneManager.getInstance().indexData(metadata);
        }
    }

    public synchronized void sync() {
        try {
            dictionary = loadJsonFile("dic.json");

            // 데이터 삽입 및 Lucene 색인 갱신
            for (Map.Entry<String, Map<String, Object>> entry : dictionary.entrySet()) {
                Map<String, Object> metadata = entry.getValue();
                metadata.put("key", entry.getKey());
                LuceneManager.getInstance().indexData(metadata);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error syncing dictionary", e);
        }
    }

    public static Map<String, Map<String, Object>> getDictionary() {
        return dictionary;
    }

    // JSON 파일 로드
    private static Map<String, Map<String, Object>> loadJsonFile(String fileName) {
        Project project = ProjectUtils.getCurrentProject();
        if (project == null) {
            throw new RuntimeException("Project not found");
        }

        PluginSettingsState state = PluginSettingsState.getInstance(project);
        if (!"".equals(state.languageFilePath)) {
            try (InputStream inputStream = new FileInputStream(state.languageFilePath + File.separator + fileName)){
                // JSON 파싱
                InputStreamReader reader = new InputStreamReader(inputStream);
                Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
                return new Gson().fromJson(reader, type);
            } catch (Exception e) {
                throw new RuntimeException("Error loading JSON file", e);
            }
        }
        return new HashMap<>();
    }

}
