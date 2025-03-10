package com.emro.configuration;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsConfigurable implements Configurable {

    private PluginSettingsComponent settingsComponent;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Caidentia Plugin Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsComponent = new PluginSettingsComponent();

        Project project = ProjectUtils.getCurrentProject();
        if (project != null) {
            PluginSettingsState state = PluginSettingsState.getInstance(project);
            settingsComponent.setSyncServiceUrl(state.syncServiceUrl);
            settingsComponent.setLanguageFilePath(state.languageFilePath);
        }

        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        Project project = ProjectUtils.getCurrentProject();
        if (project == null) {
            return false;
        }

        PluginSettingsState state = PluginSettingsState.getInstance(project);
        return !settingsComponent.getSyncServiceUrl().equals(state.syncServiceUrl) ||
                !settingsComponent.getLanguageFilePath().equals(state.languageFilePath);
    }

    @Override
    public void apply() {
        Project project = ProjectUtils.getCurrentProject();
        if (project == null) {
            return;
        }

        PluginSettingsState state = PluginSettingsState.getInstance(project);
        state.syncServiceUrl = settingsComponent.getSyncServiceUrl();
        state.languageFilePath = settingsComponent.getLanguageFilePath();
    }

    @Override
    public void reset() {
        Project project = ProjectUtils.getCurrentProject();
        if (project == null) {
            return;
        }

        PluginSettingsState state = PluginSettingsState.getInstance(project);
        settingsComponent.setSyncServiceUrl(state.syncServiceUrl);
        settingsComponent.setLanguageFilePath(state.languageFilePath);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }


}
