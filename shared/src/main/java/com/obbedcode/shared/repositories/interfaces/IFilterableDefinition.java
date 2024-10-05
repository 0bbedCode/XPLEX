package com.obbedcode.shared.repositories.interfaces;

import com.obbedcode.shared.repositories.filters.bases.FilterBase;

public interface IFilterableDefinition {
    String getFilterKind();
    boolean isFactory(IFilterFactory factory);
}
