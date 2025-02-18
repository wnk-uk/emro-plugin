package com.emro.configuration;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PluginSettingsComponent {
    private JPanel panel;
	private JBTextField languageFilePathField;
    private JBTextField syncServiceUrlField;



    public PluginSettingsComponent() {
	    languageFilePathField = new JBTextField();
	    languageFilePathField.setEditable(false);
	    syncServiceUrlField = new JBTextField();

	    // 파일 선택 버튼
	    JButton fileChooserButton = new JButton("📂");
	    fileChooserButton.addActionListener(e -> chooseDirectory(languageFilePathField));

	    // 설명 레이블
	    JBLabel descriptionLabel = new JBLabel("Dictionary directory path에 존재하는 jsonFile을 메모리에 캐시합니다.");
	    descriptionLabel.setForeground(Color.GRAY);

	    // UI 배치 (FormBuilder 활용)
	    panel = FormBuilder.createFormBuilder()
			    .addLabeledComponent("Dictionary directory path:", createFileChooserPanel(languageFilePathField, fileChooserButton))
			    .addComponent(descriptionLabel)
			    .addLabeledComponent("Dictionary sync url:", syncServiceUrlField)
			    .addComponentFillVertically(new JPanel(), 0)
			    .getPanel();
    }

	// 디렉터리 선택기
	private void chooseDirectory(JBTextField targetField) {
		FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
		descriptor.setTitle("Starting directory");
		descriptor.setDescription("Select a directory");
		descriptor.setShowFileSystemRoots(true);

		VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
		if (file != null) {
			targetField.setText(file.getPath());
		}
	}

	// 파일 선택 버튼이 포함된 패널
	private JPanel createFileChooserPanel(JBTextField textField, JButton button) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(textField, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);
		return panel;
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
