package com.obbedcode.shared.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {
    public static boolean isValid(Collection<?> collection) { return collection != null && !collection.isEmpty(); }
    public static boolean isValidArray(Object arr) { return arr != null && Array.getLength(arr) > 0; }


    public static String[] toStringArray(List<String> lst) {
        if(lst == null || lst.isEmpty()) return new String[] { };
        return lst.toArray(new String[0]);
    }
}
