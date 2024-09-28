package com.obbedcode.xplex.views.etc;

public interface OnBackPressContainer {
    void setBackController(IOnBackClickListener backController);
    IOnBackClickListener getBackController();
}