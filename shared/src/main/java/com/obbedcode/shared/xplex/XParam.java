package com.obbedcode.shared.xplex;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.utils.CommandProcessUtils;
import com.obbedcode.shared.utils.DataTypeUtils;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;

public class XParam {
    public final Map<String, String> settings;
    public final Map<String, String> properties;
    public final XHookDef hook;
    public final XC_MethodHook.MethodHookParam param;
    public final String packageName;

    public final boolean useDefault;
    public final boolean isBefore;

    private boolean mWasModified = false;
    private String mOldValue;
    private String mNewValue;
    private String mException;

    public XParam(Map<String, String> settings,
                  Map<String, String> properties,
                  XHookDef hook,
                  XC_MethodHook.MethodHookParam param,
                  String packageName,
                  boolean isBefore) {

        this.settings = settings;
        this.properties = properties;
        this.hook = hook;
        this.param = param;
        this.packageName = packageName;
        this.useDefault = DataTypeUtils.isTrueString(getSetting("base.use.default.values", "false"));
        this.isBefore = isBefore;
    }

    public boolean wasModified() { return mWasModified; }
    public String getOldValue() { return mOldValue; }
    public String getNewValue() { return mNewValue; };
    public String getException() { return mException; }

    public void finalizeUsage(String oldValue, String newValue) {
        this.mOldValue = oldValue;
        this.mNewValue = newValue;
        this.mWasModified = true;
    }

    public Object getArgument(int index) {
        if(param.args.length <= index) return null;
        return param.args[index];
    }

    public String getPropertySetting(String property) { return properties.get(property); }

    public String getSetting(String settingName) { return settingName == null ? null : settings.get(settingName); }
    public String getSetting(String settingName, String defaultValue) {
        String value = getSetting(settingName);
        return value == null && useDefault ? defaultValue : value;
    }

    public boolean isNullResult() { return param.getResult() == null; }

    public Object getThis() { return param.thisObject; }

    public Object getResult() { return param.getResult(); }
    public String getResultAsString() {
        Object res = getResult();
        return res instanceof String ? (String)res : null;
    }

    //Put these in different parts of abstraction
    public Process echoCommand(String data) {
        return null;
    }

    public String readCommandOutput() {
        Object result = getResult();
        if(result instanceof Process) return CommandProcessUtils.readProcessOutput((Process)result);
        return Str.EMPTY;
    }

    public void setResult(Object value) {
        param.setResult(value);
    }
}
