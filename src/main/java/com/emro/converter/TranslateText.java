package com.emro.converter;

import com.emro.model.InMemoryDictionary;

import java.util.Map;

public class TranslateText {

    public static String translate(String input) {
        // 용어집 로드 예제 (Map<String, Map<String, String>> 형태로 저장된 경우)
        Map<String, Map<String, String>> dictionary = InMemoryDictionary.load();

        if (dictionary.containsKey(input)) {
            String languageCode = "AA";
            //정규식에 의해서 languageCode를 만들어줌
            return dictionary.get(input).getOrDefault(languageCode, input);
        }
        return input; // 번역이 없을 경우 원래 텍스트 반환
    }

}
