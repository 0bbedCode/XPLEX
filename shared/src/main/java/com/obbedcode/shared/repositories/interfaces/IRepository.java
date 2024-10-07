package com.obbedcode.shared.repositories.interfaces;

import java.util.List;

import kotlin.Pair;

public interface IRepository<T> {
    List<T> get();
    List<T> get(int userId, String packageName, String type);
    List<T> getFilteredAndSorted(
            List<T> items,
            Pair<String, List<String>> filter,
            String keyword,
            boolean isReverse);
}
