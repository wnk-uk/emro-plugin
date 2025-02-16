package com.emro.configuration;
import javax.swing.*;
import java.awt.*;

public class PluginSettingsComponent {
    private JPanel panel;
    private JTextField syncServiceUrlField;
    private JTextField languageFilePathField;

    public PluginSettingsComponent() {
        panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Dictionary Sync Service URL:"));
        syncServiceUrlField = new JTextField();
        panel.add(syncServiceUrlField);

        panel.add(new JLabel("Language File Path:"));
        languageFilePathField = new JTextField();
        panel.add(languageFilePathField);
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getSyncServiceUrl() {
        return syncServiceUrlField.getText();
    }

    public void setSyncServiceUrl(String url) {
        syncServiceUrlField.setText(url);
    }

    public String getLanguageFilePath() {
        return languageFilePathField.getText();
    }

    public void setLanguageFilePath(String path) {
        languageFilePathField.setText(path);
    }
}
