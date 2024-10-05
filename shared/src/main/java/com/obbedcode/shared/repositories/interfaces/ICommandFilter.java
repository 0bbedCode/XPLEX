package com.obbedcode.shared.repositories.interfaces;

import com.obbedcode.shared.repositories.filters.shell.CommandData;

public interface ICommandFilter {
    boolean isCommand(CommandData data);
}
