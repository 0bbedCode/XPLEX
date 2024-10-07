package com.obbedcode.shared.repositories.filters;

import android.content.Context;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.TypeResolver;
import com.obbedcode.shared.repositories.filters.bases.FilterFactory;
import com.obbedcode.shared.repositories.filters.interfaces.IPropertiesFilterDefinition;
import com.obbedcode.shared.repositories.filters.interfaces.IShellFilterDefinition;
import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.repositories.filters.shell.FilterShellDefinition;
import com.obbedcode.shared.repositories.filters.shell.interceptors.GetPropCommandInterceptor;
import com.obbedcode.shared.repositories.filters.shell.interceptors.StatCommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.ICommandHook;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.XParam;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ShellFilterFactory extends FilterFactory {
    private static final String TAG = "ObbedCode.XP.ShellFilterFactory";

    public ShellFilterFactory() {
        this.definitions = DEFINITIONS;
        super.setFilterCategory("shell");
    }

    public static final List<XHookDefinition> DEFINITIONS = Arrays.asList(
            new ShellHookOne(),
            new ShellHookTwo(),
            new ShellHookThree(),
            new ShellHookFour(),
            new ShellHookFive(),
            new ShellHookSix(),
            new ShellHookSeven());

    public static final List<ICommandInterceptor> INTERCEPTORS = Arrays.asList(
            new GetPropCommandInterceptor(),
            new StatCommandInterceptor()
            //To Add More
    );

    public static final String AUTHOR = "ObbedCode";
    public static final String COLLECTION = "Intercept";
    public static final String GROUP = "Filter";
    public static final String CONTAINER = "Shell Commands";
    public static final String DESCRIPTION = "Intercept and Filter Commands that Get Executed on the Application";

    public static IFilterFactory createFactoryInstance() { return new ShellFilterFactory(); }
    public static void setHeader(XHookDefinition def) { def.setHeader(AUTHOR, COLLECTION, GROUP, CONTAINER, DESCRIPTION); }

    @Override
    public void handleDefinition(XHookDefinition def) {
        if(def instanceof IShellFilterDefinition) {
            IShellFilterDefinition filterDefinition = (IShellFilterDefinition)def;
            //Find the Interceptor for the Hook Definition
            //Custom Hook Def Example Mac Address
            for(ICommandInterceptor interceptor : INTERCEPTORS) {
                if(filterDefinition.getCommand().equalsIgnoreCase(interceptor.getCategory())) {
                    appCache.addCommandInterceptor(interceptor);
                    //Now we some how need to pipe the "data" over ?
                    //No we "pipe" the data over to the "XParam" it will hold what is needed
                    //In this case it will hold "properties"
                    break;
                }
            }
        }
    }

    @Override
    public void deployHooks(XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        if(appCache.hasCommandInterceptors()) {
            try {
                for (XHookDefinition shellHook : DEFINITIONS) {
                    try {
                        //Member member = shellHook.resolveMember();
                        //Member member = shellHook.re
                        TypeResolver.ResolvedHook resolved = shellHook.resolvedHook(context);
                        final Member member = resolved.resolveMember();
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
                                XLog.i(TAG, "[shell factoryy] Shell Hook Invoked!");
                                XParam xParam = appCache.createParam(param, shellHook, false);
                                CommandData data = commandHook.getCommandData(xParam);
                                if(data.isEcho()) return;
                                for(ICommandInterceptor iCom : interceptors) {
                                    if(iCom.isCommand(data)) {
                                        iCom.handleCommand(xParam, data, commandHook);
                                        break;
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

    public static class ShellHookOne extends XHookDefinition implements ICommandHook {
        public ShellHookOne() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/ProcessBuilder.start()");
            super.setMethod("start");
            super.setClass("java.lang.ProcessBuilder");
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseOne(param); }
    }

    public static class ShellHookTwo extends XHookDefinition implements ICommandHook {
        public ShellHookTwo() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(arg)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseTwo(param); }
    }

    public static class ShellHookThree extends XHookDefinition implements ICommandHook  {
        public ShellHookThree() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(arg, envp)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String", String[].class.getName());
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseThree(param); }
    }

    public static class ShellHookFour extends XHookDefinition implements ICommandHook  {
        public ShellHookFour() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(arg, envp, file)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams("java.lang.String", String[].class.getName(), "java.io.File");
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseFour(param); }
    }

    public static class ShellHookFive extends XHookDefinition implements ICommandHook  {
        public ShellHookFive() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(args)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName());
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseFive(param); }
    }

    public static class ShellHookSix extends XHookDefinition implements ICommandHook  {
        public ShellHookSix() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(args, envp)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName(), String[].class.getName());
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseSix(param); }
    }

    public static class ShellHookSeven extends XHookDefinition implements ICommandHook  {
        public ShellHookSeven() {
            ShellFilterFactory.setHeader(this);
            super.setHookId("Filter/Runtime.exec(args, envp, file)");
            super.setMethod("exec");
            super.setClass(Runtime.class.getName());
            super.setParams(String[].class.getName(), String[].class.getName(), "java.io.File");
            super.setReturnType("java.lang.Process");
        }

        @Override
        public CommandData getCommandData(XParam param) { return new CommandData().parseSeven(param); }
    }
}
