package com.obbedcode.shared.repositories.interfaces;

import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.xplex.XParam;

public interface ICommandInterceptor {
    String getCategory();
    boolean isCommand(CommandData data);
    boolean handleCommand(XParam param, CommandData com, ICommandHook iCommandHook);
}
