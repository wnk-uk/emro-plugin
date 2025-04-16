package com.emro.contributor;

import com.emro.dictionary.LuceneManager;
import com.emro.dictionary.LuceneManagerGlo;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.openapi.editor.SelectionModel;
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


	                    SelectionModel selectionModel = parameters.getEditor().getSelectionModel();
	                    String inputText = selectionModel.getSelectedText() != null ? selectionModel.getSelectedText() : "";


		                if (inputText.isEmpty()) {
			                inputText = getInputText(parameters);
		                }

	                    if (parameters.getInvocationCount() < 2 && !isKorean(inputText)) return;
						if (inputText.indexOf(".") > -1 && !"STD.".equalsIgnoreCase(inputText)) return;


	                    // 기본 sorter에 기반한 커스텀 sorter 생성
	                    CompletionSorter sorter = CompletionSorter.defaultSorter(parameters, result.getPrefixMatcher())
			                    .weighAfter("priority", new LookupElementWeigher("lucene-priority") {
				                    @Override
				                    public Comparable weigh(@NotNull LookupElement element) {
					                    // PrioritizedLookupElement라면 priority 값을 사용
					                    if (element instanceof PrioritizedLookupElement<?> ple) {
						                    return -ple.getPriority();
					                    }
					                    return 0;
				                    }
			                    });

	                    CompletionResultSet prioritizedResult = result.withRelevanceSorter(sorter);

                        if (isKorean(inputText)) {
	                        List<Map<String, Object>> completions = null;
	                        try {
		                        completions = LuceneManager.getInstance().search("ko_KR", inputText, "ngram");
	                        } catch (Exception e) {
                                return;
	                        }
	                        for (Map<String, Object> completion : completions) {
		                        prioritizedResult.addElement(
                                        PrioritizedLookupElement.withPriority(
                                                LookupElementBuilder.create((String) completion.get("ko_KR"))
                                                        .withTailText((String) "-" + completion.get("en_US") + "/" +  completion.get("ko_KR"), true)
		                                                .withTypeText((String) completion.get("source"), true)
                                                        .withInsertHandler((con, item) -> {
                                                            // 사용자가 선택했을 때 삽입될 텍스트
                                                            int startOffset = con.getStartOffset();
                                                            int tailOffset = con.getTailOffset();
                                                            String hitsText = (String) completion.get("key");
//                                                            if ("용어집".equals(completion.get("source"))) hitsText = (String) completion.get("en_US");
                                                            con.getDocument().replaceString(startOffset, tailOffset, hitsText);
                                                        }),-9999)
                                );
                            }

                        } else if (parameters.getInvocationCount() >= 2) {
							if (inputText.length() <= 1) return;

                            List<Map<String, Object>> completions = null;
	                        List<Map<String, Object>> completionsKeys = null;
                            try {
                                completions = LuceneManager.getInstance().search("en_US", inputText, "ngram2");
	                            completionsKeys =LuceneManager.getInstance().search("key", inputText, "ngram2");
                            } catch (Exception e) {
                                return;
                            }
                            for (Map<String, Object> completion : completions) {
	                            prioritizedResult.addElement(
                                        PrioritizedLookupElement.withPriority(
                                                LookupElementBuilder.create((String) completion.get("en_US"))
		                                                .withLookupString(((String) completion.get("en_US")).toUpperCase())
		                                                .withLookupString(((String) completion.get("en_US")).toLowerCase())
		                                                .withTailText((String)/* "-" + completion.get("en_US") + "/" +  */completion.get("ko_KR"), true)
                                                        .withTypeText((String) completion.get("source"), true)
                                                        .withInsertHandler((con, item) -> {
                                                            // 사용자가 선택했을 때 삽입될 텍스트
                                                            int startOffset = con.getStartOffset();
                                                            int tailOffset = con.getTailOffset();
                                                            String hitsText = (String) completion.get("key");
//                                                            if ("용어집".equals(completion.get("source"))) hitsText = (String) completion.get("en_US");
                                                            con.getDocument().replaceString(startOffset, tailOffset, hitsText);
                                                        }),-9999)
                                );
                            }

	                        for (Map<String, Object> completion : completionsKeys) {
		                        prioritizedResult.addElement(
				                        PrioritizedLookupElement.withPriority(
						                        LookupElementBuilder.create((String) completion.get("key"))
								                        .withLookupString(((String) completion.get("key")).toLowerCase())
								                        .withLookupString(((String) completion.get("key")).toUpperCase())
								                        .withTailText((String) "-" + completion.get("en_US") + "/" +  completion.get("ko_KR"), true)
								                        .withTypeText((String) completion.get("source"), true)
								                        .withInsertHandler((con, item) -> {
									                        // 사용자가 선택했을 때 삽입될 텍스트
									                        int startOffset = con.getStartOffset();
									                        int tailOffset = con.getTailOffset();
									                        String hitsText = (String) completion.get("key");
//                                                            if ("용어집".equals(completion.get("source"))) hitsText = (String) completion.get("en_US");
									                        con.getDocument().replaceString(startOffset, tailOffset, hitsText);
								                        }),-9999)
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
	    textBeforeCursor = textBeforeCursor.trim();

        // 공백이나 특수 문자 기준으로 가장 마지막 단어만 추출
        String[] tokens = textBeforeCursor.split("\\s+|<|>|\\(|\\)|\\{|\\}|\"|'");
        //return tokens.length > 0 ? tokens[tokens.length - 1] : "";
	    return tokens.length > 0 ? String.join("", tokens) : "";
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
