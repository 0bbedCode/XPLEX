package com.obbedcode.shared.xplex.data.hook;

import android.text.TextUtils;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.xplex.XParam;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;

public class XHookDef {
    public String method;
    public String className;
    public String returnType;
    public String[] paramTypes;

    public String author;
    public String collection;   //Collection like (Privacy)
    public String group;        //Group like (Identity & Tracking)
    public String container;    //Can be Null this is sub group of Group, Example MAC Address Hooks can be up to (3) so they are all grouped
    public String description;  //

    public String[] settings;   //Settings to Control its "value"
    public Kind kind = Kind.HOOK;

    public int minSdk = 1;
    public int maxSdk = 9999;

    public boolean beforeHook = false;
    public boolean afterHook = false;

    public String hookId;

    public String getHookId() {
        if(hookId == null) {
            String col = Str.cleanAsOnlyAlphabetString(collection);
            String grp = Str.cleanAsOnlyAlphabetString(group);
            String con = Str.cleanAsOnlyAlphabetString(container);

            StringBuilder sb = new StringBuilder();
            sb.append(col).append(".").append(grp).append(".").append(con).append(".").append(method).append("(");
            if(paramTypes != null) {
                int last = paramTypes.length - 1;
                for(int i = 0; i < paramTypes.length; i++) {
                    String t = paramTypes[i];
                    if(t.contains(".")) {
                        String[] ps = t.split("\\.");
                        sb.append(ps[ps.length - 1]);
                    } else {
                        sb.append(t);
                    }
                    if(i != last) {
                        sb.append(",");
                    }
                }
            }

            sb.append(")");
            hookId = sb.toString();
        }

        return hookId;
    }

    public boolean hasParams() { return paramTypes != null && paramTypes.length > 0; }
    public boolean wildCardParams() { return paramTypes != null && paramTypes[0].equals("*"); }
    public boolean isField() { return method != null && method.startsWith("#"); }

    protected void setValue(XParam param) {
        //ignore for now
    }

    public void setIsBeforeHook(boolean isBefore) { this.beforeHook = isBefore; }
    public void setIsAfterHook(boolean isAfter) { this.afterHook = isAfter; }

    public String getSetting(XParam param) { return param.getSetting(settings[0]); }
    public String getSetting(XParam param, int settingIndex) { return param.getSetting(settings[settingIndex]); }

    public Member resolveMember() {
        return null;
    }

    public void setHeader(String author, String collection, String group, String container, String description) {
        this.author = author;
        this.collection = collection;
        this.group = group;
        this.container = container;
        this.description = description;
    }

    public void setReturnType(String returnType) {
        if(returnType != null) {
            this.returnType = returnType.trim();
        }
    }

    public void setClass(String className) {
        if(className != null) {
            this.className = className.trim();
        }
    }

    public void setMethod(String method) {
        if(method != null) {
            this.method = method.trim();
        }
    }

    public void setSettings(String... sets) {
        List<String> s = new ArrayList<>();
        if(sets != null) {
            for(String set : sets) {
                if(!TextUtils.isEmpty(set)) {
                    s.add(set.trim());
                }
            }
        }

        this.settings = s.toArray(new String[0]);
    }

    public void setParams(String... params) {
        List<String> p = new ArrayList<>();
        if(params != null) {
            for(String pm : params) {
                if(!TextUtils.isEmpty(pm)) {
                    p.add(pm.trim());
                }
            }
        }

        this.paramTypes = p.toArray(new String[0]);
    }

    public boolean handleHooked(XParam param) {
        throw new NotImplementedError();
    }

    public enum Kind {
        UNKNOWN(0),
        HOOK(1),
        FILTER_FILE(2),
        FILTER_QUERY(3),
        FILTER_CALL(4),
        FILTER_TRANSACT(5),
        FILTER_PROPERTIES(6),
        FILTER_SHELL(7),
        FILTER_SETTINGS(8);

        public static Kind parse(Integer code) {
            if(code == null) return UNKNOWN;
            if(code >= Kind.values().length || code < -1) return Kind.UNKNOWN;
            return Kind.values()[code];
        }

        private final int value;
        Kind(int value) { this.value = value; }
        public int getValue() { return value; }
    }
}
