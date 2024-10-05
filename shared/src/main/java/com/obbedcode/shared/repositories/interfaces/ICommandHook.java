package com.obbedcode.shared.repositories.interfaces;

import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.xplex.XParam;

public interface ICommandHook {
    CommandData getCommandData(XParam param);
}
