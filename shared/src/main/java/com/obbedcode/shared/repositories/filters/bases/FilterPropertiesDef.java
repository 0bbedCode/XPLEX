package com.obbedcode.shared.repositories.filters.bases;

import android.text.TextUtils;

import com.obbedcode.shared.repositories.filters.PropertiesFilterFactory;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.repositories.interfaces.IFilterableDefinition;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.ArrayList;
import java.util.List;

public class FilterPropertiesDef extends XHookDef implements IFilterableDefinition {
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
        return "*properties*";
    }

    @Override
    public boolean isFactory(IFilterFactory factory) {
        return factory instanceof PropertiesFilterFactory;
    }
}
