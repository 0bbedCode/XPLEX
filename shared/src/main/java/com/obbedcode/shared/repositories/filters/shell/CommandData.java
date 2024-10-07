package com.obbedcode.shared.repositories.filters.shell;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.XParam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandData {
    private static final String TAG = "ObbedCode.XP.CommandData";

    public String[] commands;
    public String[] environments;
    public File file;

    public boolean isEcho() {
        if(commands == null) return false;
        int z = Math.min(3, commands.length);
        for(int i = 0; i < z; i++) {
            if(commands[i].startsWith("echo"))
                return true;
        }

        return false;
    }

    //Split the Arg!!!

    public CommandData() { }
    public CommandData parseOne(XParam param) {
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
            commands = comParts.toArray(new String[0]);
            return data;
        }catch (Exception e) {
            XLog.e(TAG, "[.start()] Error Getting Command Data: " + e.getMessage());
            return null;
        }
    }

    public CommandData parseTwo(XParam param) {
        String com = tryGetStringAt(param);
        commands = com == null ? new String[] { "" } : com.split(" ");
        return this;
    }

    public CommandData parseThree(XParam param) {
        String com = tryGetStringAt(param);
        commands = com == null ? new String[] { "" } : com.split(" ");
        environments = tryGetStringArrayAt(param, 1);
        return this;
    }

    public CommandData parseFour(XParam param) {
        String com = tryGetStringAt(param);
        commands = com == null ? new String[] { "" } : com.split(" ");
        environments = tryGetStringArrayAt(param, 1);
        file = tryGetFileAt(param);
        return this;
    }

    public CommandData parseFive(XParam param) {
        commands = tryGetStringArrayAt(param, 0);
        return this;
    }

    public CommandData parseSix(XParam param) {
        commands = tryGetStringArrayAt(param, 0);
        environments = tryGetStringArrayAt(param, 1);
        return this;
    }

    public CommandData parseSeven(XParam param) {
        commands = tryGetStringArrayAt(param, 0);
        environments = tryGetStringArrayAt(param, 1);
        file = tryGetFileAt(param);
        return this;
    }

    private String[] tryGetStringArrayAt(XParam param, int index) {
        try {return (String[])param.getArgument(index);
        }catch (Exception ignored) { return new String[]{ }; }
    }

    private String tryGetStringAt(XParam param) {
        try { return (String)param.getArgument(0);
        }catch (Exception ignored) { return ""; }
    }

    private File tryGetFileAt(XParam param) {
        try { return (File)param.getArgument(2);
        }catch (Exception ignored) { return null; }
    }

    public boolean hasCommandLineArg(String[] args) {
        if(commands != null && args != null) {
            for(String c : commands) {
                for(String a : args) {
                    if(c.equalsIgnoreCase(a))
                        return true;
                }
            }
        }
        return false;
    }
}
