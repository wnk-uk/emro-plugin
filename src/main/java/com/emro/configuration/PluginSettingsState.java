package com.emro.configuration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;

@State(
        name = "com.emro.configuration.PluginSettingsState",
        storages = @Storage("PluginSettings.xml") // 설정이 저장될 XML 파일명
)
@Service(Service.Level.APP)
public final class PluginSettingsState implements PersistentStateComponent<PluginSettingsState> {

    public String syncServiceUrl = ""; // 기본 URL
    public String languageFilePath = ""; // 기본 저장 경로
	public String tokenPath = ""; // 기본 저장 경로

    public static PluginSettingsState getInstance(Project project) {
        //return project.getService(PluginSettingsState.class);
	    return ApplicationManager.getApplication().getService(PluginSettingsState.class);
    }

    @Override
    public PluginSettingsState getState() {
        return this; // 현재 설정 상태 반환
    }

    @Override
    public void loadState(PluginSettingsState state) {
        this.syncServiceUrl = state.syncServiceUrl;
        this.languageFilePath = state.languageFilePath;
		this.tokenPath = state.tokenPath;
    }
}
