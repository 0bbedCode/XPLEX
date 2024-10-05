package com.obbedcode.shared.repositories.filters;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.filters.bases.FilterBase;
import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.repositories.filters.shell.FilterShellDef;
import com.obbedcode.shared.repositories.filters.shell.interceptors.GetPropCommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.ICommandHook;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.XParam;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ShellFilterFactory extends FilterBase {
    private static final String TAG = "ObbedCode.XP.ShellFilterFactory";

    public static final List<XHookDef> DEFINITIONS = Arrays.asList(
            new ShellHookOne(),
            new ShellHookTwo(),
            new ShellHookThree(),
            new ShellHookFour(),
            new ShellHookFive(),
            new ShellHookSix(),
            new ShellHookSeven());

    public static final List<ICommandInterceptor> INTERCEPTORS = Arrays.asList(
            new GetPropCommandInterceptor()
            //To Add More
    );

    public static final String AUTHOR = "ObbedCode";
    public static final String COLLECTION = "Intercept";
    public static final String GROUP = "Filter";
    public static final String CONTAINER = "Shell Commands";
    public static final String DESCRIPTION = "Intercept and Filter Commands that Get Executed on the Application";

    public static IFilterFactory createFactoryInstance() { return new ShellFilterFactory(); }
    public static void setHeader(XHookDef def) { def.setHeader(AUTHOR, COLLECTION, GROUP, CONTAINER, DESCRIPTION); }

    @Override
    public void handleDefinition(XHookDef def) {
        if(isFilter(def) && def instanceof FilterShellDef) {
            FilterShellDef filterDef = (FilterShellDef) def;
            for(ICommandInterceptor interceptor : INTERCEPTORS) {
                if(interceptor.getCategory().equalsIgnoreCase(filterDef.commandInterceptor)) {
                    appCache.addCommandInterceptor(interceptor);
                }
            }
        }
    }

    @Override
    public void deployHooks(XC_LoadPackage.LoadPackageParam lpparam) {
        if(appCache.hasCommandInterceptors()) {
            try {
                for (XHookDef shellHook : DEFINITIONS) {
                    try {
                        Member member = shellHook.resolveMember();
                        XposedBridge.hookMethod(member, new XC_MethodHook() {
                            final List<ICommandInterceptor> interceptors = appCache.interceptors;
                            final ICommandHook commandHook = (ICommandHook)shellHook;

                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                //XParam xParam = appCache.createParam(param, shellHook, true);
                                //CommandData data = commandHook.getCommandData(xParam);
                                //for(ICommandInterceptor iCom : interceptors) {
                                //    if(iCom.isCommand(data)) {
                                //        iCom.handleCommand(xParam, data, commandHook);
                                //    }
                                //}
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                XParam xParam = appCache.createParam(param, shellHook, false);
                                CommandData data = commandHook.getCommandData(xParam);
                                for(ICommandInterceptor iCom : interceptors) {
                                    if(iCom.isCommand(data)) {
                                        iCom.handleCommand(xParam, data, commandHook);
                                    }
                                }
                            }
                        });
                    }catch (Exception eInner) {
                        XLog.e(TAG, "Failed to Hook method: " + eInner + " => " + shellHook);
                    }
                }
            }catch (Exception e) {
                XLog.e(TAG, "Error Deploying Hooks... " + e);
            }
        }
    }

    public static class ShellHookOne extends XHookDef implements ICommandHook {
        public static final String TAG = "ObbedCode.XP.ShellFilter$ShellHookOne";
        public ShellHookOne() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("start");
            super.setClass("java.lang.ProcessBuilder");
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) {
            try {
                ProcessBuilder pb = (ProcessBuilder)param.getThis();
                CommandData data = new CommandData();
                List<String> comParts = new ArrayList<>();
                for(String c : pb.command()) {
                    String cTrimmed = c.trim();
                    if(cTrimmed.contains(" ")) {
                        String[] parts = cTrimmed.split(" ");
                        for(String p : parts) {
                            comParts.add(p.trim());
                        }
                    } else {
                        comParts.add(cTrimmed);
                    }
                }

                data.parts = comParts.toArray(new String[0]);
                return data;
            }catch (Exception e) {
                XLog.e(TAG, "Error Getting Command Data: " + e.getMessage());
                return null;
            }
        }
    }

    public static class ShellHookTwo extends XHookDef {
        public ShellHookTwo() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.Process");
        }
    }

    public static class ShellHookThree extends XHookDef {
        //Make a part for getting data for next part ??
        public ShellHookThree() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String", String[].class.getName());
            super.setReturnType("java.lang.Process");
        }
    }

    public static class ShellHookFour extends XHookDef {
        public ShellHookFour() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String", String[].class.getName(), "java.io.File");
            super.setReturnType("java.lang.Process");
        }
    }

    public static class ShellHookFive extends XHookDef {
        public ShellHookFive() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName());
            super.setReturnType("java.lang.Process");
        }
    }

    public static class ShellHookSix extends XHookDef {
        public ShellHookSix() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName(), String[].class.getName());
            super.setReturnType("java.lang.Process");
        }
    }

    public static class ShellHookSeven extends XHookDef {
        public ShellHookSeven() {
            ShellFilterFactory.setHeader(this);
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName(), String[].class.getName(), "java.io.File");
            super.setReturnType("java.lang.Process");
        }
    }
}
