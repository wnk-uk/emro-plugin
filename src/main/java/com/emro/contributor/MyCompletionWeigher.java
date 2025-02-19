package com.emro.contributor;

import com.intellij.codeInsight.completion.CompletionLocation;
import com.intellij.codeInsight.completion.CompletionWeigher;
import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

public class MyCompletionWeigher extends CompletionWeigher {
    @Override
    public Comparable weigh(@NotNull LookupElement element, @NotNull CompletionLocation location) {
        // Java 기본 자동완성보다 낮은 가중치 부여
        return -99999;
    }
}
