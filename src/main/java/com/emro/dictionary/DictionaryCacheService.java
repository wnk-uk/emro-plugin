package com.emro.dictionary;

import com.emro.configuration.PluginSettingsState;
import com.emro.configuration.ProjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class DictionaryCacheService {

    private final Project project;
    private static Map<String, Map<String, Object>> dictionary;
    private static Map<String, Map<String, Object>> glossary;

    public DictionaryCacheService(Project project) throws Exception {
        this.project = project;

	    // 비동기 진행률 표시
	    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Multilanguage Synchronization Progress...") {
              @Override
              public void run(@NotNull ProgressIndicator indicator) {
	              indicator.setText("Multilanguage Synchronization Progress...");
	              indicator.setFraction(0.0);
	              try {
	                  // Dictionary 동기화 실행
	                  DictionaryCacheService service = project.getService(DictionaryCacheService.class);
	                  if (service != null) {
	                      if (!service.sync(false)) return;
	                  }

	                  // 진행률 업데이트 (예제용 대기 시간 추가)
	                  for (int i = 1; i <= 5; i++) {
	                      Thread.sleep(500);
	                      indicator.setFraction(i / 5.0);
	                  }

	                  // 동기화 완료 알림
	                  Notification notification = NotificationGroupManager.getInstance()
	                          .getNotificationGroup("DictionarySyncNotification")
	                          .createNotification("Multilanguage Synchronization Completed", NotificationType.INFORMATION);
	                  notification.notify(project);

	              } catch (Exception e) {
	                  Notification notification = NotificationGroupManager.getInstance()
	                          .getNotificationGroup("DictionarySyncNotification")
	                          .createNotification("Multilanguage Synchronization Failed: " + e.getMessage(), NotificationType.ERROR);
	                  notification.notify(project);
	              }
              }
      });
    }

    public synchronized boolean sync(boolean isLoad) {
        try {
            fetchJson();
	        LuceneManager.getInstance().refresh();
	        LuceneManagerGlo.getInstance().refresh();
        } catch (Exception e) {
            Notification notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup("DictionarySyncNotification")
                    .createNotification("Check Configuration Sync Path: " + e.getMessage(), NotificationType.ERROR);
            notification.notify(project);
            return false;
        }

        try {
	        if (dictionary == null || isLoad) {
		        dictionary = loadJsonFile("dic.json");
	        }
            if (glossary == null || isLoad) {
                glossary = loadJsonFile("glo.json");
            }

            // 데이터 삽입 및 Lucene 색인 갱신
            for (Map.Entry<String, Map<String, Object>> entry : dictionary.entrySet()) {
                Map<String, Object> metadata = entry.getValue();
                metadata.put("key", entry.getKey());
	            if (metadata.get("ko_KR") == null || "".equals(metadata.get("ko_KR"))) metadata.put("ko_KR", "ko_KR Not Register");
	            if (metadata.get("en_US") == null || "".equals(metadata.get("en_US"))) metadata.put("en_US", "en_US Not Register");
                LuceneManager.getInstance().indexData(metadata);
            }

            for (Map.Entry<String, Map<String, Object>> entry : glossary.entrySet()) {
                Map<String, Object> metadata = entry.getValue();
                metadata.put("key", entry.getKey());
	            if (metadata.get("ko_KR") == null || "".equals(metadata.get("ko_KR"))) metadata.put("ko_KR", "ko_KR Not Register");
	            if (metadata.get("en_US") == null || "".equals(metadata.get("en_US"))) metadata.put("en_US", "en_US Not Register");
                LuceneManagerGlo.getInstance().indexData(metadata);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error syncing dictionary", e);
        }

        return true;
    }

    public static Map<String, Map<String, Object>> getDictionary() {
        return dictionary;
    }

	public static Map<String, Map<String, Object>> getGlossary() {
		return glossary;
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

    public void fetchJson() throws IOException {
        String[] filenames = {"multilang.json", "glo.json"};
        PluginSettingsState state = PluginSettingsState.getInstance(project);

        for (String filename : filenames) {
            URL url = new URL(state.syncServiceUrl + "/api/down/resource/" + filename);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to fetch glo.json: HTTP " + responseCode);
            }
            Path dirPath = Paths.get(state.languageFilePath);
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(filename);

            try (InputStream in = conn.getInputStream();
                 OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                in.transferTo(out);
            } finally {
                conn.disconnect();
            }
        }
    }

}
