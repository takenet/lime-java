package org.limeprotocol.util;

import org.apache.commons.codec.binary.Base64;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringUtils {

    public static final String STRING_EMPTY = "";

    public static boolean isNullOrEmpty(String string){
        return string == null || string.equals("");
    }

    public static boolean isNullOrWhiteSpace(String string){
        return isNullOrEmpty(string) || string.trim().length() == 0;
    }

    public static String format(String pattern, Object... values){

        Map<String, Object> tags = new LinkedHashMap<String, Object>();

        for (int i=0; i<values.length; i++){
            tags.put("\\{" + i + "\\}", values[i]==null ? "" : values[i]);
        }

        String formatted = pattern;
        for (Map.Entry<String, Object> tag : tags.entrySet()) {
            // bottleneck, creating temporary String objects!
            formatted = formatted.replaceAll(tag.getKey(), tag.getValue().toString());
        }
        return formatted;
    }

    public static String trimEnd(String string, String finalCharacter){
        string.trim();
        if(string.endsWith(finalCharacter)){
            return string.substring(0, string.length()-1);
        }

        return string;
    }

    public static String toBase64(String value) {
        return new String(Base64.encodeBase64(value.getBytes()));
    }

    public static String toCamelCase(String s){

        String[] parts = s.split("_");

        //All caps without '_'
        if(parts.length == 0){
            return s.toLowerCase();
        }

        String camelCaseString = "";
        for (int i = 0; i< parts.length; i++){

            if(i == 0){
                camelCaseString = parts[i].toLowerCase();
            } else {
                camelCaseString = camelCaseString + toProperCase(parts[i]);
            }
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}


