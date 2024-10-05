package com.obbedcode.shared.xplex.database;

import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.shared.xplex.data.XStartupSetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPrivacyControl {
    private static final List<XHookGroup> cachedGroup = new ArrayList<>();


    public static boolean putStartupSetting(Integer user, String category, String receiverName, Integer state, boolean deleteSetting) { return putStartupSetting(XStartupSetting.create(user, category, receiverName, state), deleteSetting); }
    public static boolean putStartupSetting(XStartupSetting startupSetting, boolean deleteSetting) { return XDatabaseManager.instance.putStartupSetting(startupSetting, deleteSetting); }

    public static boolean putSetting(Integer userId, String packageName, String settingName, String value, boolean deleteSetting) { return putSetting(XSetting.create(userId, packageName, settingName, value), deleteSetting); }
    public static boolean putSetting(XSetting setting, boolean deleteSetting) { return XDatabaseManager.instance.putSetting(setting, deleteSetting); }

    public static XSetting getSetting(Integer userId, String packageName, String settingName) { return XDatabaseManager.instance.getSetting(userId, packageName, settingName); }

    public static Collection<XSetting> getSettings(Integer userId, String packageName, String settingName) { return XDatabaseManager.instance.getSettings(userId, packageName, settingName); }

    public static XStartupSetting getStartupSetting(Integer userId, String packageName, String receiverName) { return XDatabaseManager.instance.getStartupSetting(userId, packageName, receiverName); }

    public static Collection<XStartupSetting> getStartupSettings(Integer userId, String packageName) { return XDatabaseManager.instance.getStartupSettings(userId, packageName); }

    /*
        Assignments
     */
    public static Collection<XAssignment> getAssignments(Integer userId, String packageName) { return XDatabaseManager.instance.getAssignments(userId, packageName); }
    public static boolean putAssignment(Integer userId, String packageName, String hook, XAssignment.Kind extra, boolean deleteAssignment) { return XDatabaseManager.instance.putAssignment(userId, packageName, hook, extra, deleteAssignment);  }
    public static boolean putAssignment(XAssignment assignment, boolean deleteAssignment) { return XDatabaseManager.instance.putAssignment(assignment, deleteAssignment); }

    public static Collection<XHookGroup> getHookGroups() {
        if(cachedGroup.isEmpty()) {

        }

        return null;
    }
}
