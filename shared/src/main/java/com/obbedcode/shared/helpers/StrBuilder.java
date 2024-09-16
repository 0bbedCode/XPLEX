package com.obbedcode.shared.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.utils.RuntimeUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StrBuilder {
    //Not really chars but I like the name
    public static final String STR_NEW_LINE = "\n";
    public static final String STR_SPACE = " ";
    public static final String STR_TAB = "\t";
    public static final String STR_COMMA = ",";
    public static final String STR_COLLEN = ":";

    public static StrBuilder create() { return new StrBuilder(); }
    public static StrBuilder create(int capacity) { return new StrBuilder(capacity); }
    public static StrBuilder create(String str) { return new StrBuilder(str); }
    public static StrBuilder create(String str, boolean newLine) { return new StrBuilder(str, newLine); }

    private final StringBuilder mSb;

    public StrBuilder() { mSb = new StringBuilder(); }
    public StrBuilder(int capacity) { mSb = new StringBuilder(capacity); }
    public StrBuilder(String str) { mSb = new StringBuilder(str); }
    public StrBuilder(String str, boolean newLine) {
        mSb = new StringBuilder(str);
        if(newLine) mSb.append(STR_NEW_LINE);
    }

    public StringBuilder getInternalBuilder() { return mSb; }

    public StrBuilder newLine() {
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder space() {
        mSb.append(STR_SPACE);
        return this;
    }

    public StrBuilder tab() {
        mSb.append(STR_TAB);
        return this;
    }

    public StrBuilder collen() {
        mSb.append(STR_COLLEN);
        return this;
    }

    public StrBuilder comma() {
        mSb.append(STR_COMMA);
        return this;
    }

    public StrBuilder appendLine(StrBuilder sb) {
        mSb.append(sb.toString());
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(StrBuilder sb) {
        mSb.append(sb.toString());
        return this;
    }

    public StrBuilder appendLine(StringBuilder sb) {
        mSb.append(sb.toString());
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(StringBuilder sb) {
        mSb.append(sb.toString());
        return this;
    }

    public StrBuilder appendLine(String s) {
        mSb.append(s);
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(String s) {
        mSb.append(s);
        return this;
    }

    public StrBuilder appendLine(Character c) {
        mSb.append(c);
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(Character c) {
        mSb.append(c);
        return this;
    }

    public StrBuilder appendLine(Boolean b) {
        mSb.append(b);
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(Boolean b) {
        mSb.append(String.valueOf(b));
        return this;
    }

    public StrBuilder appendLine(Integer i) {
        mSb.append(i);
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(Integer i) {
        mSb.append(i);
        return this;
    }

    public StrBuilder appendLine(Object o) {
        mSb.append(o);
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(Object o) {
        mSb.append(o);
        return this;
    }

    public StrBuilder appendAsLines(String[] arr) { append(arr, STR_NEW_LINE); mSb.append(STR_NEW_LINE); return this;  }
    public StrBuilder appendLine(String[] arr) { return appendLine(arr, STR_SPACE); }
    public StrBuilder appendLine(String[] arr, String delimiter) {
        mSb.append(Str.joinArray(arr, delimiter));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(String[] arr) { return append(arr, STR_SPACE); }
    public StrBuilder append(String[] arr, String delimiter) {
        mSb.append(Str.joinArray(arr, delimiter));
        return this;
    }

    public StrBuilder appendAsLines(List<String> lst) { append(lst, STR_NEW_LINE); mSb.append(STR_NEW_LINE); return this; }
    public StrBuilder appendLine(List<String> lst) { return appendLine(lst, STR_SPACE); }
    public StrBuilder appendLine(List<String> lst, String delimiter) {
        mSb.append(Str.joinList(lst, delimiter));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(List<String> lst) { return append(lst, STR_SPACE); }
    public StrBuilder append(List<String> lst, String delimiter) {
        mSb.append(Str.joinList(lst, delimiter));
        return this;
    }

    public StrBuilder appendErrorLine(Throwable t, boolean stackTrace) {
        mSb.append("Exception: ")
                .append(t.getMessage());
        if(stackTrace)
            mSb.append(STR_NEW_LINE)
                    .append("Stack Trace:")
                    .append(STR_NEW_LINE)
                    .append(RuntimeUtils.getStackTraceSafeString(t));

        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendError(Throwable t, boolean stackTrace) {
        mSb.append("Exception: ")
                .append(t.getMessage());
        if(stackTrace)
            mSb.append(STR_NEW_LINE)
                    .append("Stack Trace:")
                    .append(STR_NEW_LINE)
                    .append(RuntimeUtils.getStackTraceSafeString(t));

        return this;
    }

    public StrBuilder appendFieldLine(String fieldName, Object value) {
        mSb.append(fieldName)
                .append(STR_COLLEN)
                .append(STR_SPACE)
                .append(value)
                .append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendField(String fieldName, Object value) {
        mSb.append(fieldName)
                .append(STR_COLLEN)
                .append(STR_SPACE)
                .append(value);
        return this;
    }


    public StrBuilder appendFieldLine(String fieldName, String value) {
        if(value != null && value.endsWith("\n")) value = value.substring(0, value.length() - 1);
        mSb.append(fieldName)
                .append(STR_COLLEN)
                .append(STR_SPACE)
                .append(value)
                .append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendField(String fieldName, String value) {
        mSb.append(fieldName)
                .append(STR_COLLEN)
                .append(STR_SPACE)
                .append(value);
        return this;
    }

    public StrBuilder appendStrBytesLine(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendStrBytes(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs));
        return this;
    }

    public StrBuilder appendStrBytesLine(byte[] bs, Charset charSet) {
        if(bs == null) return this;
        mSb.append(new String(bs, charSet));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendStrBytes(byte[] bs, Charset charSet) {
        if(bs == null) return this;
        mSb.append(new String(bs, charSet));
        return this;
    }

    public StrBuilder appendStrBytesASCIILine(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.US_ASCII));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendStrBytesASCII(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.US_ASCII));
        return this;
    }

    public StrBuilder appendStrBytesUTF16Line(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.UTF_16));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder appendStrBytesUTF16(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.UTF_16));
        return this;
    }

    public StrBuilder appendStrBytesUTF8Line(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.UTF_8));
        mSb.append(STR_NEW_LINE);
        return this;
    }


    public StrBuilder appendStrBytesUTF8(byte[] bs) {
        if(bs == null) return this;
        mSb.append(new String(bs, StandardCharsets.UTF_8));
        return this;
    }

    public StrBuilder appendLine(byte[] bs) {
        if(bs == null) return this;
        mSb.append(Str.bytesToHex(bs));
        mSb.append(STR_NEW_LINE);
        return this;
    }

    public StrBuilder append(byte[] bs) {
        if(bs == null) return this;
        mSb.append(Str.bytesToHex(bs));
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof String) {
            String s = (String)obj;
            return s.equalsIgnoreCase(this.toString());
        }

        if(obj instanceof StringBuilder) {
            StringBuilder s = (StringBuilder)obj;
            return s.toString().equalsIgnoreCase(this.toString());
        }

        if(obj instanceof StrBuilder) {
            StrBuilder s = (StrBuilder) obj;
            return s.toString().equalsIgnoreCase(this.toString());
        }

        return false;
    }

    @NonNull
    @Override
    public String toString() {
        String s = mSb.toString();
        if(s.endsWith("\n")) return s.substring(0, s.length() - 1);
        return s;
    }
}
