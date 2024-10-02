package com.obbedcode.shared.xplex.interfaces;

import com.obbedcode.shared.xplex.data.XUser;

public interface IXUserContainer {
    XUser getTargetUser();
    void setTargetUser(XUser user);
}
