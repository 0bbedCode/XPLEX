package com.obbedcode.shared.utils;

import android.content.Context;

import java.io.File;

public class MultiUserUtils {
    //Xomi user 999 for clones
    public static boolean isMainUserDirectory(File directory) { return isMainUserDirectory(directory.getAbsoluteFile()); }
    public static boolean isMainUserDirectory(String directory) { return directory != null && directory.startsWith("/data/user/0/"); }
}
