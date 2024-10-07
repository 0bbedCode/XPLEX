package com.obbedcode.shared.repositories.filters.shell.interceptors;

import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.repositories.interfaces.ICommandHook;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.xplex.XParam;

import java.util.ArrayList;
import java.util.List;

public class GetPropCommandInterceptor implements ICommandInterceptor {
    public String category = "getprop";

    public String[] commandRegex = new String[] { "getprop", "build.prop" };
    @Override
    public boolean isCommand(CommandData data) {
        boolean foundCat = false;
        if(data != null && data.commands != null) {
            for(String p : data.commands) {
                if(p.equalsIgnoreCase(commandRegex[0]))
                    return true;
                if(p.equalsIgnoreCase("cat"))
                    foundCat = true;
                if(foundCat && p.contains(commandRegex[1]))
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean handleCommand(XParam param, CommandData com, ICommandHook iCommandHook) {
        boolean foundGetProp = false;
        boolean foundGrep = false;
        List<String> cleanedParts = new ArrayList<>();
        for(String part : com.commands) {
            if(!foundGetProp && (part.equalsIgnoreCase("getprop") || part.contains("build.prop"))) {
                foundGetProp = true;
            } else {
                if(part.equalsIgnoreCase("grep") || part.equalsIgnoreCase("cat")) foundGrep = true;
                else if(!(part.equals("|") || part.equals(">") || part.equals(">>") || part.equals("<") || part.equals("<<"))) cleanedParts.add(part);
            }
        }

        if(!foundGrep && !cleanedParts.isEmpty()) {
            //if Grep like "ro." ??...
            for(String c : cleanedParts) {
                String low = c.toLowerCase();
                String propSetting = param.getPropertySetting(low);
                if(propSetting != null) {
                    String newValue = param.getSetting(propSetting);
                    param.setResult(param.echoCommand(newValue == null ? "" : newValue));
                }
            }
        } else {
            //Dude this is pretty baller >:)
            //Get full output then replace with new
            String res = param.readCommandOutput();
            String[] parts = res.split("\n");
            StringBuilder newResult = new StringBuilder();
            for(String p : parts) {
                if(p.length() > 3 && p.contains(" ")) {
                    String pCleaned = p.replaceAll("\\[", "").replaceAll("]", "");
                    boolean needsBrackets = pCleaned.length() != p.length();
                    pCleaned = pCleaned.trim();

                    StringBuilder low = new StringBuilder();
                    StringBuilder midChar = new StringBuilder();
                    StringBuilder high = new StringBuilder();

                    boolean foundSpacer = false;
                    boolean isInSpacer = false;
                    char[] chars = pCleaned.toCharArray();
                    for(int i = 0; i < chars.length; i++) {
                        char c = chars[i];
                        if(!foundSpacer) {
                            if(c == ' ' || c == '\t') {
                                foundSpacer = true;
                                isInSpacer = true;
                                midChar.append(c);
                            } else {
                                low.append(c);
                            }
                        } else {
                            if(isInSpacer) {
                                if(c == ' ' || c == '\t')
                                    midChar.append(c);
                                else {
                                    isInSpacer = false;
                                    high.append(c);
                                }
                            } else {
                                high.append(c);
                            }
                        }
                    }

                    String lowProp = low.toString().toLowerCase();
                    String propSetting = param.getPropertySetting(lowProp);
                    if(propSetting != null) {
                        String newValue = param.getSetting(propSetting);
                        if(needsBrackets) newResult.append("[");
                        newResult.append(low);
                        if(needsBrackets) newResult.append("]");
                        newResult.append(midChar);
                        if(needsBrackets) newResult.append("[");
                        newResult.append(newValue);
                        if(needsBrackets) newResult.append("]");
                        newResult.append("\n");
                    } else {
                        newResult.append(p);
                        newResult.append("\n");
                    }
                }
            }

            param.setResult(param.echoCommand(newResult.toString()));
        }

        return false;
    }

    @Override
    public String getCategory() { return category; }
}
