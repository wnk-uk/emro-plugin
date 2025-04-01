package com.emro.converter;

import com.emro.dictionary.DictionaryCacheService;
import com.emro.dictionary.LuceneManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

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
            Map<String, Object> completion = null;
            try {
                //completions = LuceneManager.getInstance().search("key", selectedText, "keyword");
                completion = DictionaryCacheService.getDictionary().getOrDefault(selectedText, null);
	            if (completion == null || completion.isEmpty()) completion = DictionaryCacheService.getGlossary().getOrDefault(selectedText, null);
                if (completion != null && !completion.isEmpty()) {
                    // 검색 결과를 창에 표시
                    showSearchResults(completion);
                } else {
                    Messages.showInfoMessage("Not searched.", "Not Result");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void showSearchResults(Map<String, Object> completion) {
        showAlertForSelection((String) completion.get("ko_KR"), (String) completion.get("en_US"));
        // JList로 검색 결과 표시
//        DefaultListModel<String> listModel = new DefaultListModel<>();
//
//        JList<String> resultList = new JBList<>(listModel);
//        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        // JScrollPane으로 감싸기
//        JScrollPane scrollPane = new JBScrollPane(resultList);
//        scrollPane.setPreferredSize(new Dimension(400, 200));
//
//        // 선택 이벤트 핸들러
//        resultList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                String selectedValue = resultList.getSelectedValue();
//                if (selectedValue != null) {
//                    // Alert 창 띄우기
//                    showAlertForSelection(selectedValue);
//                }
//            }
//        });
//
//        // 팝업 창 생성
//        JFrame frame = new JFrame("검색 결과");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
    }

    private void showAlertForSelection(String kor, String en) {
        Messages.showInfoMessage("ko_KR: " + kor + "\n" + "en_US: " + en, "Information");
    }
}
