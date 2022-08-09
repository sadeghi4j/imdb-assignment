package com.imdb.imdbtitles.util;

public class ParseUtil {

    public static Integer getInteger(String field) {
        return field.equals("\\N") ? null : Integer.parseInt(field);
    }

    public static Boolean getBoolean(String field) {
        return field.equals("\\N") ? null : "1".equals(field);
    }

}
