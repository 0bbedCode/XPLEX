package com.obbedcode.shared.hook.gum;

import com.obbedcode.shared.io.FileApi;
import com.obbedcode.shared.io.FileEx;

import java.io.File;
import java.io.FileDescriptor;
import java.nio.charset.StandardCharsets;

public class FileGum {
    private static final String FILE_TAG = "ObbedCode.XP.FileGum";

    public static boolean fileOrDirectoryExists(String fileOrDirectory) { return new FileEx(fileOrDirectory).exists(); }
    public static FileDescriptor createTempFileDescriptor(String contents) { return FileApi.generateFakeFileDescriptor(contents); }
    public static File createTempFile(String contents) { return FileApi.generateTempFakeFile(contents); }
    public static String fileContents(String file) { return new FileEx(file).readFileContents( StandardCharsets.UTF_8); }
    public static FileEx getFile(String file) { return new FileEx(file); }
}
