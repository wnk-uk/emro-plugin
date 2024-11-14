package com.emro.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

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

                        // 파일 확장자가 HTML인지 확인

                        // 부모 요소가 특정 태그인지 검사
                        XmlTag tag = findParentTag(element);

                        // 태그가 없는 상태에서 '<' 다음에 위치한 경우
                        String textBeforeCursor = element.getTextOffset() > 0
                                ? file.getText().substring(element.getTextOffset() - 1, element.getTextOffset())
                                : "";

                        if ("<".equals(textBeforeCursor)) {
                            // 추천할 태그 목록
                            result.addElement(LookupElementBuilder.create("sc-labal")
                                    .withTypeText("Custom HTML Tag", true)
                                    .withTailText(" - A custom component", true));
                            result.addElement(LookupElementBuilder.create("sc-grid")
                                    .withTypeText("Custom HTML Tag", true)
                                    .withTailText(" - A custom web component", true));
                            result.addElement(LookupElementBuilder.create("sc-text-field")
                                    .withTypeText("Custom HTML Tag", true)
                                    .withTailText(" - A widget container", true));
                        }

                        if (file.getName().endsWith(".html")) {
                            // 추천 키워드 추가
                            if (tag != null && "sc-label".equals(tag.getName())) {
                                result.addElement(LookupElementBuilder.create("data-example")
                                        .withTypeText("HTML Attribute", true)
                                        .withTailText(" - Example attribute", true));
                                result.addElement(LookupElementBuilder.create("custom-attribute")
                                        .withTypeText("HTML Attribute", true)
                                        .withTailText(" - Custom attribute", true));
                            }
                        }
                    }
                });
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
}
