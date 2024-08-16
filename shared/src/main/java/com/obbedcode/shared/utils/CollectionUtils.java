package com.obbedcode.shared.utils;

import java.util.Collection;

public class CollectionUtils {
    public static boolean isValid(Collection<?> collection) { return collection != null && !collection.isEmpty(); }

}
