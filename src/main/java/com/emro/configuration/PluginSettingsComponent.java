package com.emro.configuration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PluginSettingsComponent {
    private JPanel panel;
    private JTextField syncServiceUrlField;
    private JTextField languageFilePathField;

    private JButton startDirectoryButton;
    private JButton environmentVariablesButton;


    public PluginSettingsComponent() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

//        panel.add(new JLabel("Dictionary Sync Service URL:"));
//        syncServiceUrlField = new JTextField();
//        panel.add(syncServiceUrlField);
//
//        panel.add(new JLabel("Dictionary File Path:"));
//        languageFilePathField = new JTextField();
//        panel.add(languageFilePathField);


        // 📂 Start Directory (비활성화된 입력 필드 + 폴더 선택 버튼)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Start directory:"), gbc);

        gbc.gridx = 1;
        languageFilePathField = new JTextField("C:\\study\\test-pro");
        languageFilePathField.setEditable(false);
        panel.add(languageFilePathField, gbc);

        gbc.gridx = 2;
        startDirectoryButton = new JButton("\uD83D\uDCC1"); // 📁 아이콘 대신 사용할 수 있음
        startDirectoryButton.setPreferredSize(new Dimension(40, 25));
        panel.add(startDirectoryButton, gbc);

        // 🌍 Environment Variables (입력 필드 + 설정 버튼)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Environment variables:"), gbc);

        gbc.gridx = 1;
        syncServiceUrlField = new JTextField();
        panel.add(syncServiceUrlField, gbc);

        gbc.gridx = 2;
        // 📂 Start Directory 선택 버튼 동작 추가
        startDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(panel);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    languageFilePathField.setText(selectedFolder.getAbsolutePath());
                }
            }
        });

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
