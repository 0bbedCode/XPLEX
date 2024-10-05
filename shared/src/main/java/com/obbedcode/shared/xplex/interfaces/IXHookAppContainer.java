package com.obbedcode.shared.xplex.interfaces;

import com.obbedcode.shared.xplex.data.hook.XHookApp;

public interface IXHookAppContainer {
    XHookApp getHookApplication();
    void setHookApplication(XHookApp application);
}
