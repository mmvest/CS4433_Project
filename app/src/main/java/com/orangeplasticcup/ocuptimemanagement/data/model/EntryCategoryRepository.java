package com.orangeplasticcup.ocuptimemanagement.data.model;

public class EntryCategoryRepository {
    private static String[] categoryEntries = null;

    public static void bindCategories(String[] categories) throws Exception{
        if(categories == null) throw new NullPointerException();
        if(categoryEntries != null) throw new Exception("Attempted to rebind categories");
        categoryEntries = categories;
    }

    public static String[] getCategories() {
        if(categoryEntries == null) throw new NullPointerException("Categories was not bound");
        return categoryEntries;
    }
}
