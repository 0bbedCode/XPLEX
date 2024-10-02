package com.obbedcode.shared.repositories;

import com.obbedcode.shared.random.tools.RandomData;
import com.obbedcode.shared.repositories.interfaces.IRepository;
import com.obbedcode.shared.xplex.data.XHookGroup;
import com.obbedcode.shared.xplex.data.XIdentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Pair;

public class HookRepository implements IRepository<XHookGroup> {
    public static final HookRepository INSTANCE = new HookRepository();

    @Override
    public List<XHookGroup> get() { return getGroupHooksForApp(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE); }

    @Override
    public List<XHookGroup> get(int userId, String packageName) {
        return getGroupHooksForApp(userId, packageName);
    }

    @Override
    public List<XHookGroup> getFilteredAndSorted(List<XHookGroup> items, Pair<String, List<String>> filter, String keyword, boolean isReverse) {
        return getFilteredAndSortedGroups(items, filter, keyword, isReverse);
    }

    //public static List<XApp> getFilteredAndSortedApps(
    //        List<XApp> apps,
    //        Pair<String, List<String>> filter,
    //        String keyword,
    //        boolean isReverse

    //
    public static List<XHookGroup> getGroupHooksForApp(int userId, String packageName) {
        List<XHookGroup> data = new ArrayList<>();

        for(int i = 0; i < RandomData.generateNumber(8, 30); i++) {
            XHookGroup g = new XHookGroup();
            g.name = RandomData.generateNumberString(5);
            g.id = RandomData.generateNumberString(5);
            g.description = RandomData.generateNumberString(5);
            data.add(g);
        }


        return data;
    }

    public static List<XHookGroup> getFilteredAndSortedGroups(
            List<XHookGroup> groups,
            Pair<String, List<String>> filter,
            String keyword,
            boolean isReverse) {

        if(groups == null || groups.isEmpty()) return new ArrayList<>();    //Better than a Null ptr
        Comparator<XHookGroup> comparator = getComparator(filter.getFirst(), isReverse);
        List<XHookGroup> filteredGroups = new ArrayList<>();
        for(XHookGroup g : groups) {
            if (isAppMatchingCriteria(g, keyword, filter.getSecond()))
                filteredGroups.add(g);
        }

        Collections.sort(filteredGroups, comparator);
        return filteredGroups;
    }

    private static boolean isAppMatchingCriteria(XHookGroup group, String keyword, List<String> filterCriteria) {
        boolean keywordMatch = keyword.isEmpty() || group.name.toLowerCase().contains(keyword.toLowerCase());
        if (!keywordMatch) return false;
        if(!filterCriteria.isEmpty()) {
            //do
            for (String criteria : filterCriteria) {
            /*switch (criteria) {
                case UiGlobals.FILTER_CONFIGURED: //已配置
                    return false;//For now
                //if (!app.isEnabled) return false;   //This is wrong ??, yes
                //break;
                case UiGlobals.FILTER_LAST_UPDATE:    //最近更新
                    if (System.currentTimeMillis() - app.lastUpdateTime >= 3 * 24 * 3600 * 1000L) return false;
                    break;
                case UiGlobals.FILTER_DISABLED:     //已禁用
                    if (app.isEnabled) return false;
                    break;
            }*/
            }
        }

        return true;
    }

    public static Comparator<XHookGroup> getComparator(String sortBy, boolean isReverse) {
        Comparator<XHookGroup> comparator;
        switch (sortBy) {
            default:
                comparator = new Comparator<XHookGroup>() {
                    @Override
                    public int compare(XHookGroup a1, XHookGroup a2) { return String.CASE_INSENSITIVE_ORDER.compare(a1.name, a2.name); }
                };
                break;
        }

        if (isReverse) {
            final Comparator<XHookGroup> finalComparator = comparator;
            comparator = new Comparator<XHookGroup>() {
                @Override
                public int compare(XHookGroup a1, XHookGroup a2) { return finalComparator.compare(a2, a1); }
            };
        }

        return comparator;
    }
}
