package com.obbedcode.shared.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionUtils {
    public static boolean isValid(Collection<?> collection) { return collection != null && !collection.isEmpty(); }
    public static boolean isValidArray(Object arr) { return arr != null && Array.getLength(arr) > 0; }

}
