package com.emro.contributor;

import com.emro.dictionary.LuceneManager;
import com.emro.dictionary.LuceneManagerGlo;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AutoCompletionContributor extends CompletionContributor {
    public AutoCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiFile file = parameters.getOriginalFile();
                        PsiElement element = parameters.getPosition();

                        // 현재 입력값 가져오기
                        String inputText = getInputText(parameters);

                        if (isKorean(inputText)) {
	                        List<Map<String, Object>> completions = null;
	                        try {
		                        completions = LuceneManager.getInstance().search("ko_KR", inputText, "ngram");
	                        } catch (Exception e) {
                                return;
	                        }

	                        for (Map<String, Object> completion : completions) {
                                result.addElement(
                                        PrioritizedLookupElement.withPriority(
                                                LookupElementBuilder.create((String) completion.get("ko_KR") + "(" + completion.get("source") + ")")
                                                        .withTypeText((String) completion.get("source"), true)
                                                        .withTailText((String) "-" + completion.get("en_US"), true)
                                                        .withInsertHandler((con, item) -> {
                                                            // 사용자가 선택했을 때 삽입될 텍스트
                                                            int startOffset = con.getStartOffset();
                                                            int tailOffset = con.getTailOffset();
                                                            String hitsText = (String) completion.get("key");
                                                            if ("용어집".equals(completion.get("source"))) hitsText = (String) completion.get("en_US");
                                                            con.getDocument().replaceString(startOffset, tailOffset, hitsText);
                                                        }),-99999)
                                );
                            }

                            // 강제로 자동 트리거
                            result.restartCompletionOnAnyPrefixChange();
                            result.restartCompletionWhenNothingMatches();
                        } else {
                            List<Map<String, Object>> completions = null;
                            try {
                                completions = LuceneManager.getInstance().search("en_US", inputText, "ngram");
                            } catch (Exception e) {
                                return;
                            }
                            for (Map<String, Object> completion : completions) {
                                result.addElement(
                                        PrioritizedLookupElement.withPriority(
                                                LookupElementBuilder.create((String) completion.get("ko_KR") + "(" + completion.get("source") + ")")
                                                        .withTypeText((String) completion.get("source"), true)
                                                        .withTailText((String) "-" + completion.get("en_US"), true)
                                                        .withInsertHandler((con, item) -> {
                                                            // 사용자가 선택했을 때 삽입될 텍스트
                                                            int startOffset = con.getStartOffset();
                                                            int tailOffset = con.getTailOffset();
                                                            String hitsText = (String) completion.get("key");
                                                            if ("용어집".equals(completion.get("source"))) hitsText = (String) completion.get("en_US");
                                                            con.getDocument().replaceString(startOffset, tailOffset, hitsText);
                                                        }),-99999)
                                );
                            }

                            // 부모 요소가 특정 태그인지 검사
                            if (file.getName().endsWith(".java")) {

                            } else if (file.getName().endsWith(".html")) {

                            }
//                            else {
//                                // 태그가 없는 상태에서 '<' 다음에 위치한 경우
//                                String textBeforeCursor = element.getTextOffset() > 0
//                                        ? file.getText().substring(element.getTextOffset() - 1, element.getTextOffset())
//                                        : "";
//
//                                if ("<".equals(textBeforeCursor)) {
//                                    // 추천할 태그 목록
//                                    result.addElement(LookupElementBuilder.create("sc-labal")
//                                            .withTypeText("Custom HTML Tag", true)
//                                            .withTailText(" - A custom component", true));
//                                    result.addElement(LookupElementBuilder.create("sc-grid")
//                                            .withTypeText("Custom HTML Tag", true)
//                                            .withTailText(" - A custom web component", true));
//                                    result.addElement(LookupElementBuilder.create("sc-text-field")
//                                            .withTypeText("Custom HTML Tag", true)
//                                            .withTailText(" - A widget container", true));
//                                }
//                            }
                        }
                    }
                });

    }



    private String getInputText(@NotNull CompletionParameters parameters) {
        int offset = parameters.getOffset(); // 현재 커서 위치
        CharSequence text = parameters.getEditor().getDocument().getCharsSequence();

        // 커서 이전 텍스트 추출 (최대 20자)
        int startOffset = Math.max(0, offset - 20);
        String textBeforeCursor = text.subSequence(startOffset, offset).toString();

        // 공백이나 특수 문자 기준으로 가장 마지막 단어만 추출
        String[] tokens = textBeforeCursor.split("\\s+|<|>|\\(|\\)|\\{|\\}|\"|'");
        return tokens.length > 0 ? tokens[tokens.length - 1] : "";
    }

    /**
     * 현재 위치에서 상위 XML 태그를 찾는 유틸리티 메서드.
     *
     * @param element 현재 위치한 PsiElement
     * @return XmlTag 부모 태그
     */
    private XmlTag findParentTag(PsiElement element) {
        PsiElement parent = element.getParent();
        while (parent != null && !(parent instanceof XmlTag)) {
            parent = parent.getParent();
        }
        return (XmlTag) parent;
    }

    private boolean isKorean(String text) {
        return text.matches(".*[가-힣]+.*"); // 한글 포함 여부 확인
    }
}
