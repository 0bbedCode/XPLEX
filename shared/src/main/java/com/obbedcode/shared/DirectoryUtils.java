package com.obbedcode.shared;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class DirectoryUtils {




    public static String getOldDirectory() {
        return Environment.getDataDirectory() + File.separator +
                "system" + File.separator +
                "xlua";
    }
}
