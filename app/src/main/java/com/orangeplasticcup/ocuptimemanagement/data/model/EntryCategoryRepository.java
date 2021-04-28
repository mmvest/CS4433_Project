package com.orangeplasticcup.ocuptimemanagement.data.model;

import android.graphics.Color;

import java.util.HashMap;

public class EntryCategoryRepository {
    private static final HashMap<String, Integer> colorMap = new HashMap<>();
    private static String[] categoryEntries = null;

    public static void bindCategories(String[] categories) throws Exception{
        if(categories == null) throw new NullPointerException();
        if(categoryEntries != null) throw new Exception("Attempted to rebind categories");
        categoryEntries = categories;
        for(String str : categories) {
            colorMap.put(str, ((int)(Math.random()*16777215)) | (0xFF << 24));
        }
    }

    public static String[] getCategories() {
        if(categoryEntries == null) throw new NullPointerException("Categories was not bound");
        return categoryEntries;
    }

    public static int getColor(String str) {
        if(colorMap.containsKey(str)) return colorMap.get(str);
        else return Color.BLACK;
    }
}
