package com.obbedcode.shared.repositories.filters;

import com.obbedcode.shared.repositories.filters.bases.FilterBase;
import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDef;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.Arrays;
import java.util.List;

//Extend XHookDef ?? its a hook def technically
public class PropertiesFilterFactory extends FilterBase {
    //We dont need to get "properties" right away more so lets use this "filter"
    //It will handle everything we need to handle creating us a Properties List
    //Tnen Deploying Hooks...

    public static final String AUTHOR = "ObbedCode";
    public static final String COLLECTION = "Intercept";
    public static final String GROUP = "Filter";
    public static final String CONTAINER = "Device Properties";
    public static final String DESCRIPTION = "Intercept and Filter Properties from get prop / build.prop";

    public static void setHeader(XHookDef def) { def.setHeader(AUTHOR, COLLECTION, GROUP, CONTAINER, DESCRIPTION); }

    public static IFilterFactory createFactoryInstance() { return new PropertiesFilterFactory(); }
    public static final List<XHookDef> DEFINITIONS = Arrays.asList(
            new PropertiesHookOne(),
            new PropertiesHookTwo(),
            new PropertiesHookThree(),
            new PropertiesHookFour());

    public PropertiesFilterFactory() {
        this.definitions = DEFINITIONS;
        super.setFilterCategory("*properties*");
    }

    @Override
    public void handleDefinition(XHookDef def) {
        if(isFilter(def) && def instanceof FilterPropertiesDef) {
            super.handleDefinition(def);
        }
    }

    public static class PropertiesHookOne extends XHookDef {
        public PropertiesHookOne() {
            PropertiesFilterFactory.setHeader(this);
            super.setMethod("getProperty");
            super.setClass("java.lang.System");
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class PropertiesHookTwo extends XHookDef {
        public PropertiesHookTwo() {
            PropertiesFilterFactory.setHeader(this);
            super.setMethod("getProperty");
            super.setClass("java.lang.System");
            super.setParams("java.lang.String", "java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class  PropertiesHookThree extends XHookDef {
        public PropertiesHookThree() {
            PropertiesFilterFactory.setHeader(this);
            super.setMethod("get");
            super.setClass("android.os.SystemProperties");
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class  PropertiesHookFour extends XHookDef {
        public PropertiesHookFour() {
            PropertiesFilterFactory.setHeader(this);
            super.setMethod("get");
            super.setClass("android.os.SystemProperties");
            super.setParams("java.lang.String", "java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    //Some how add command interceptor from here ?
    //Then handler here ????
}
