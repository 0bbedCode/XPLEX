package com.obbedcode.shared.repositories.filters.shell;

import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDef;
import com.obbedcode.shared.repositories.interfaces.ICommandFilter;
import com.obbedcode.shared.xplex.XParam;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

public class FilterShellDef extends FilterPropertiesDef {
    public String commandInterceptor;
    public void setCommandInterceptor(String commandInterceptor) { this.commandInterceptor = commandInterceptor;  }
}
