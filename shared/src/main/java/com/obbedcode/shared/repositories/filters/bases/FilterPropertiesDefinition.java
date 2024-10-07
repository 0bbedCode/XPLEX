package com.obbedcode.shared.repositories.filters.bases;

import android.text.TextUtils;

import com.obbedcode.shared.repositories.filters.PropertiesFilterFactory;
import com.obbedcode.shared.repositories.filters.ShellFilterFactory;
import com.obbedcode.shared.repositories.filters.interfaces.IPropertiesFilterDefinition;
import com.obbedcode.shared.repositories.filters.interfaces.IShellFilterDefinition;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.repositories.interfaces.IFilterableDefinition;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.util.ArrayList;
import java.util.List;

//BaseDef for HookDef
//      => FilterPropertiesDef
//              => IShellFilterDef
//              => IPropertiesFilterDef (Not needed since that is the base)
//              => IFilterableDef
//
//

public class FilterPropertiesDefinition extends XHookDefinition implements
        IShellFilterDefinition,
        IPropertiesFilterDefinition,
        IFilterableDefinition {
    public String[] properties;

    public void setProperties(String... properties) {
        List<String> ps = new ArrayList<>();
        if(properties != null) {
            for(String s : properties) {
                if(!TextUtils.isEmpty(s)) {
                    ps.add(s.trim());
                }
            }
        }

        this.properties = ps.toArray(new String[0]);
    }

    @Override
    public String getFilterKind() {
        return "properties|shell";
    }

    @Override
    public boolean isFactory(IFilterFactory factory) { return factory instanceof PropertiesFilterFactory || factory instanceof ShellFilterFactory; }

    @Override
    public String[] getProperties() { return properties; }

    @Override
    public String getCommand() { return "getprop"; }
}
