package com.emro.dictionary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

    public static Map<String, Map<String, Object>> getDictionary() {
        return dictionary;
    }

    // JSON 파일 로드
    private static Map<String, Map<String, Object>> loadJsonFile(String fileName) {
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
