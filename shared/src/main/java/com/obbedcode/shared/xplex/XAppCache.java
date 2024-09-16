package com.obbedcode.shared.xplex;

import com.obbedcode.shared.xplex.data.XStartupSetting;

import java.util.HashMap;

public class XAppCache {
    public String packageName;

    //Maybe we should not cache all of this ? can have usage impacts
    //Ye we didnt cache in the original version
    //When the app needs it settings we grab them then send it to them leaving it to the app to clear cache or not

    //Still have an option to cache ?? I like this concept
    //public HashMap<Integer, XStartupSetting> startupCache = new HashMap<>();
    //public
}
