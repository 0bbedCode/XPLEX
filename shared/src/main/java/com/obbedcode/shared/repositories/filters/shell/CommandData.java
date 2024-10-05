package com.obbedcode.shared.repositories.filters.shell;

import com.obbedcode.shared.xplex.XParam;

public class CommandData {
    public String[] parts;
    public CommandData() { }
    public CommandData(String data) {
        //parse
    }

    public CommandData(XParam param) {

    }

    public boolean hasCommandLineArg(String[] args) {
        if(parts != null && args != null) {
            for(String c : parts) {
                for(String a : args) {
                    if(c.equalsIgnoreCase(a))
                        return true;
                }
            }
        }
        return false;
    }
}
