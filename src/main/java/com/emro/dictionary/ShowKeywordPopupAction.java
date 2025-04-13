package com.emro.dictionary;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowKeywordPopupAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) return;

        String inputText = getWordBeforeCursor(editor).trim();
        if (inputText.isEmpty()) return;

        try {
            List<Map<String, Object>> completions;
            if (isKorean(inputText)) {
                completions = LuceneManagerGlo.getInstance().search("ko_KR", inputText, "ngram");
            } else {
                completions = LuceneManagerGlo.getInstance().search("en_US", inputText, "ngram2");
                completions.addAll(LuceneManagerGlo.getInstance().search("key", inputText, "ngram2"));
            }
            List<String> displayList = completions.stream()
                    .map(item -> item.get("key") + "/" + item.get("ko_KR") + "/" + item.get("en_US") + " (" + item.get("source") + ")")
                    .collect(Collectors.toList());


            JBList<String> list = new JBList<>(displayList);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            new PopupChooserBuilder<>(list)
                    .setTitle("Glossary List")
                    .setItemChoosenCallback(() -> {
                        int selectedIndex = list.getSelectedIndex();
                        if (selectedIndex != -1) {
                            String keywordToInsert = (String) completions.get(selectedIndex).get("key");

                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                int offset = editor.getCaretModel().getOffset();
                                Document document = editor.getDocument();

                                // 기존 단어 범위 파악
                                int start = offset - 1;
                                CharSequence chars = document.getCharsSequence();
                                while (start >= 0) {
                                    char ch = chars.charAt(start);
                                    if (!Character.isLetterOrDigit(ch) && ch != '_') break;
                                    start--;
                                }
                                int wordStart = start + 1;

                                // 치환
                                document.replaceString(wordStart, offset, keywordToInsert);
                                editor.getCaretModel().moveToOffset(wordStart + keywordToInsert.length());
                            });
                        }
                    })
                    .createPopup()
                    .showInBestPositionFor(editor);
        } catch (Exception ex) {}
    }

    private String getWordBeforeCursor(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();
        Document document = editor.getDocument();
        int offset = caretModel.getOffset();

        if (offset == 0) return "";

        CharSequence chars = document.getCharsSequence();
        int start = offset - 1;
        while (start >= 0) {
            char ch = chars.charAt(start);
            if (!Character.isLetterOrDigit(ch) && ch != '_') break;
            start--;
        }
        return chars.subSequence(start + 1, offset).toString(); // 현재 단어 추출
    }

    private boolean isKorean(String text) {
        return text.matches(".*[가-힣]+.*"); // 한글 포함 여부 확인
    }
}
