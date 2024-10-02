package com.obbedcode.shared.repositories;

import com.obbedcode.shared.repositories.interfaces.IRepository;
import com.obbedcode.shared.xplex.data.XIdentity;
import com.obbedcode.shared.xplex.data.XSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Pair;

public class SettingRepository implements IRepository<XSetting> {
    public static final SettingRepository INSTANCE = new SettingRepository();

    @Override
    public List<XSetting> get() {
        return getSettingsForApp(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE);
    }

    @Override
    public List<XSetting> get(int userId, String packageName) {
        return getSettingsForApp(userId, packageName);
    }

    @Override
    public List<XSetting> getFilteredAndSorted(List<XSetting> items, Pair<String, List<String>> filter, String keyword, boolean isReverse) {
        return getFilteredAndSortedSettings(items, filter, keyword, isReverse);
    }


    public static List<XSetting> getSettingsForApp(int userId, String packageName) {
        return new ArrayList<>();
    }

    public static List<XSetting> getFilteredAndSortedSettings(
            List<XSetting> settings,
            Pair<String, List<String>> filter,
            String keyword,
            boolean isReverse) {

        if(settings == null || settings.isEmpty()) return new ArrayList<>();    //Better than a Null ptr
        Comparator<XSetting> comparator = getComparator(filter.getFirst(), isReverse);
        List<XSetting> filteredGroups = new ArrayList<>();
        for(XSetting s : settings) {
            if (isAppMatchingCriteria(s, keyword, filter.getSecond()))
                filteredGroups.add(s);
        }

        Collections.sort(filteredGroups, comparator);
        return filteredGroups;
    }

    private static boolean isAppMatchingCriteria(XSetting settings, String keyword, List<String> filterCriteria) {
        boolean keywordMatch = keyword.isEmpty() || settings.name.toLowerCase().contains(keyword.toLowerCase());
        if (!keywordMatch) return false;
        if(!filterCriteria.isEmpty()) {
            //do
            for (String criteria : filterCriteria) {

            }
        }

        return true;
    }

    public static Comparator<XSetting> getComparator(String sortBy, boolean isReverse) {
        Comparator<XSetting> comparator;
        switch (sortBy) {
            default:
                comparator = new Comparator<XSetting>() {
                    @Override
                    public int compare(XSetting a1, XSetting a2) { return String.CASE_INSENSITIVE_ORDER.compare(a1.name, a2.name); }
                };
                break;
        }

        if (isReverse) {
            final Comparator<XSetting> finalComparator = comparator;
            comparator = new Comparator<XSetting>() {
                @Override
                public int compare(XSetting a1, XSetting a2) { return finalComparator.compare(a2, a1); }
            };
        }

        return comparator;
    }
}
