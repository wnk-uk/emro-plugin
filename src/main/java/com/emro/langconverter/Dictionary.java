package com.emro.langconverter;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private static Map<String, Map<String, String>> dictionary;

    public static Map<String, Map<String, String>> load() {
        if (dictionary == null) {
            //dic 가져오기
            dictionary = new HashMap<>();
            dictionary.put("ABC", new HashMap<>(){
                    {
                        put("AA", "BB");
                    }
            });
        }
        
        return dictionary;
    }



}
