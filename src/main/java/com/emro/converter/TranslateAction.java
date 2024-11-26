package com.emro.converter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;

public class TranslateAction extends AnAction {

    // actionPerformed 메서드 오버라이드
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Editor와 선택 모델 가져오기
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();

        if (selectedText != null) {
            // 선택된 텍스트에 대한 작업 (예: 번역 적용)
            String translatedText = TranslateText.translate(selectedText);

            // 변경 사항을 적용
            WriteCommandAction.runWriteCommandAction(e.getProject(), () ->
                    editor.getDocument().replaceString(
                            selectionModel.getSelectionStart(),
                            selectionModel.getSelectionEnd(),
                            translatedText
                    )
            );
        }
    }
}
