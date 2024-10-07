package com.obbedcode.shared.repositories.interfaces;

public interface IFilterableDefinition {
    String getFilterKind();
    boolean isFactory(IFilterFactory factory);
}
