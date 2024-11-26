package com.emro.contributor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class KoreanTypedHandler extends TypedHandlerDelegate {

    @Override
    public Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        // 한글 입력 여부 확인
        // 공백으로 들어온 경우 한글인지 재확인
        if (charTyped == ' ' || charTyped == '\u0000') { // 공백 또는 null 문자
            System.out.println("Ignoring incomplete input: " + charTyped);
            return Result.CONTINUE;
        }

        if (isKorean(charTyped)) {
            // 자동완성 팝업 트리거
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
            return Result.STOP; // 팝업 실행 후 기본 동작 중단
        }
        return Result.CONTINUE;
    }

    private boolean isKorean(char charTyped) {
        // 입력된 문자가 한글인지 확인
        return charTyped >= 0xAC00 && charTyped <= 0xD7A3; // 한글 유니코드 범위
    }
}
