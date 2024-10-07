package com.obbedcode.shared.repositories.filters.shell;

import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDefinition;

public class FilterShellDefinition extends FilterPropertiesDefinition {
    //Move this to some definitions name space / folder
    public String commandInterceptor;
    public void setCommandInterceptor(String commandInterceptor) { this.commandInterceptor = commandInterceptor;  }
}
