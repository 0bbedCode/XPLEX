package com.obbedcode.shared.reflect;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class TypeResolver {
    private static final String TAG = "ObbedCode.XP.HookResolver";

    public static class ResolvedHook {
        private static final String TAG = "ObbedCode.XP.HookResolver$ResolveHook";
        public Class<?> targetClass;
        public String targetMethod;
        public Class<?>[] parameters;
        public Class<?> returnType;

        public boolean errorResolving = false;
        public boolean isField = false;
        public boolean isConstructor = false;

        public void setTargetMethod(String method) {
            if(method == null) isConstructor = true;
            else {
                if(method.startsWith("#")) {
                    isField = true;
                    targetMethod = method.substring(1);
                } else {
                    targetMethod = method;
                }
            }
        }

        public Member resolveMember() {
            try {
                return TypeResolver.resolveMember(targetClass, targetMethod, parameters);
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Resolve Member: " + this + " Error: " + e);
                return null;
            }
        }

        public boolean hasTargetMethod() { return targetMethod != null; }
        public boolean hasParameters() { return parameters != null && parameters.length > 0; }
        public boolean hasReturn() { return returnType != null; }

        @NonNull
        @Override
        public String toString() {
            return StrBuilder.create()
                    .appendField("Method", targetMethod)
                    .toString();
        }
    }

    public static ResolvedHook resolveHook(XHookDefinition hook, Context context) {
        ResolvedHook resolved = new ResolvedHook();
        try {
            resolved.targetClass = Class.forName(hook.getResolvedClassName(), false, context.getClassLoader());
            resolved.setTargetMethod(hook.getMethod());
            resolved.parameters = resolveParameters(hook.paramTypes, context);
            resolved.returnType = resolveType(hook.returnType, context.getClassLoader());
            return resolved;
        }catch (Exception e) {
            XLog.e(TAG, "Error Resolving the Hook: " + e);
            resolved.errorResolving = true;
            return resolved;
        }
    }

    public static Class<?>[] resolveParameters(String[] paramTypes, Context context) throws ClassNotFoundException {
        if(paramTypes == null || paramTypes.length == 0) return null;
        Class<?>[] types = new Class[paramTypes.length];
        for(int i = 0; i < paramTypes.length; i++)
            types[i] = resolveType(paramTypes[i], context.getClassLoader());

        return types;
    }

    public static Member resolveMember(Class<?> cls, String name, Class<?>[] params) throws NoSuchMethodException {
        if(cls == null)
            throw new NoSuchMethodException("Class is NULL for the Method...");

        boolean exists = false;
        try {
            Class<?> c = cls;
            //hmm ?
            while (!c.equals(Object.class))
                try {
                    if (name == null || TextUtils.isEmpty(name))
                        return c.getDeclaredConstructor(params);
                    else
                        return c.getDeclaredMethod(name, params);
                } catch (NoSuchMethodException ex) {
                    for (Member member : name == null ? c.getDeclaredConstructors() : c.getDeclaredMethods()) {
                        if (name != null && !name.equals(member.getName()))
                            continue;

                        exists = true;

                        Class<?>[] mparams = (name == null
                                ? ((Constructor) member).getParameterTypes()
                                : ((Method) member).getParameterTypes());

                        if (mparams.length != params.length)
                            continue;

                        boolean same = true;
                        for (int i = 0; i < mparams.length; i++) {
                            if (!mparams[i].isAssignableFrom(params[i])) {
                                same = false;
                                break;
                            }
                        }
                        if (!same)
                            continue;

                        Log.i(TAG, "Resolved member=" + member);
                        return member;
                    }
                    c = c.getSuperclass();
                    if (c == null)
                        throw ex;
                }
            throw new NoSuchMethodException(name);
        } catch (NoSuchMethodException ex) {
            Class<?> c = cls;
            while (c != null && !c.equals(Object.class)) {
                Log.i(TAG, c.toString());
                for (Member member : name == null ? c.getDeclaredConstructors() : c.getDeclaredMethods())
                    if (!exists || name == null || name.equals(member.getName()))
                        Log.i(TAG, "    " + member.toString());
                c = c.getSuperclass();
            }
            throw ex;
        }
    }

    public static Class<?> resolveType(String type, ClassLoader loader) throws ClassNotFoundException {
        if(type == null) return null;
        //Make this more complex
        if ("boolean".equalsIgnoreCase(type) || "bool".equalsIgnoreCase(type))
            return boolean.class;
        else if ("byte".equalsIgnoreCase(type))
            return byte.class;
        else if ("char".equalsIgnoreCase(type))
            return char.class;
        else if ("short".equalsIgnoreCase(type))
            return short.class;
        else if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type))
            return int.class;
        else if ("long".equalsIgnoreCase(type))
            return long.class;
        else if ("float".equalsIgnoreCase(type))
            return float.class;
        else if ("double".equalsIgnoreCase(type))
            return double.class;

        else if ("boolean[]".equalsIgnoreCase(type))
            return boolean[].class;
        else if ("byte[]".equalsIgnoreCase(type))
            return byte[].class;
        else if ("char[]".equalsIgnoreCase(type))
            return char[].class;
        else if ("short[]".equalsIgnoreCase(type))
            return short[].class;
        else if ("int[]".equalsIgnoreCase(type))
            return int[].class;
        else if ("long[]".equalsIgnoreCase(type))
            return long[].class;
        else if ("float[]".equalsIgnoreCase(type))
            return float[].class;
        else if ("double[]".equalsIgnoreCase(type))
            return double[].class;

        else if ("void".equalsIgnoreCase(type))
            return Void.TYPE;

        else
            return Class.forName(type, false, loader);
    }


}
