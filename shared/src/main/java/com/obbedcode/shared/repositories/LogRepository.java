package com.obbedcode.shared.repositories;

import com.obbedcode.shared.repositories.interfaces.IRepository;
import com.obbedcode.shared.xplex.data.XIdentity;
import com.obbedcode.shared.xplex.data.XLogLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Pair;

public class LogRepository implements IRepository<XLogLog> {
    public static final LogRepository INSTANCE = new LogRepository();

    @Override
    public List<XLogLog> get() {
        return getLogsForApp(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE, XLogLog.Code.UNKNOWN);
    }

    @Override
    public List<XLogLog> get(int userId, String packageName) {
        return getLogsForApp(userId, packageName, XLogLog.Code.UNKNOWN);
    }

    @Override
    public List<XLogLog> getFilteredAndSorted(List<XLogLog> items, Pair<String, List<String>> filter, String keyword, boolean isReverse) {
        return getFilteredAndSortedGroups(items, filter, keyword, isReverse);
    }


    public static List<XLogLog> getLogsForApp(int userId, String packageName, XLogLog.Code code) {
        return new ArrayList<>();
    }

    public static List<XLogLog> getFilteredAndSortedGroups(
            List<XLogLog> logs,
            Pair<String, List<String>> filter,
            String keyword,
            boolean isReverse) {

        if(logs == null || logs.isEmpty()) return new ArrayList<>();    //Better than a Null ptr
        Comparator<XLogLog> comparator = getComparator(filter.getFirst(), isReverse);
        List<XLogLog> filteredGroups = new ArrayList<>();
        for(XLogLog g : logs) {
            if (isAppMatchingCriteria(g, keyword, filter.getSecond()))
                filteredGroups.add(g);
        }

        Collections.sort(filteredGroups, comparator);
        return filteredGroups;
    }

    private static boolean isAppMatchingCriteria(XLogLog logs, String keyword, List<String> filterCriteria) {
        boolean keywordMatch = keyword.isEmpty() || logs.title.toLowerCase().contains(keyword.toLowerCase());
        if (!keywordMatch) return false;
        if(!filterCriteria.isEmpty()) {
            //do
            for (String criteria : filterCriteria) {

            }
        }

        return true;
    }

    public static Comparator<XLogLog> getComparator(String sortBy, boolean isReverse) {
        Comparator<XLogLog> comparator;
        switch (sortBy) {
            default:
                comparator = new Comparator<XLogLog>() {
                    @Override
                    public int compare(XLogLog a1, XLogLog a2) { return String.CASE_INSENSITIVE_ORDER.compare(a1.title, a2.title); }
                };
                break;
        }

        if (isReverse) {
            final Comparator<XLogLog> finalComparator = comparator;
            comparator = new Comparator<XLogLog>() {
                @Override
                public int compare(XLogLog a1, XLogLog a2) { return finalComparator.compare(a2, a1); }
            };
        }

        return comparator;
    }
}
