package com.obbedcode.xplex.views.etc;

import java.util.List;

import kotlin.Pair;

public interface IOnClearClickListener {
    void onClearAll();
    void search(String keyword);
    void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse);
}


